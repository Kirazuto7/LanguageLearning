package com.example.language_learning.mapper.util;

import com.example.language_learning.enums.SanitizationPattern;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class AIResponseSanitizer {
    private final ObjectMapper objectMapper;

    /**
     * Sanitizes a sentence by trimming whitespace and removing potential leading/trailing quotes
     * that the AI might add. This is less destructive than {@link #sanitizeEnglishField(String)}
     * and preserves internal punctuation and parentheticals.
     *
     * @param sentence The sentence to sanitize.
     * @return The sanitized sentence, or null if the input was null.
     */
    public String sanitizeEnglishSentence(String sentence) {
        if (sentence == null) {
            return null;
        }
        String sanitized = sentence.trim();
        if (sanitized.startsWith("\"") && sanitized.endsWith("\"") && sanitized.length() > 1) {
            sanitized = sanitized.substring(1, sanitized.length() - 1);
        }
        return removeInvalidEnglishCharacters(sanitized);
    }

    public String sanitizeEnglishField(String text) {
        if (text == null) {return null;}

        // This regex removes any characters inside parentheses, including the parentheses themselves,
        // and then trims any leading/trailing whitespace.
        String sanitizedOutput = text.replaceAll("\\s*\\([^)]*\\)", "");
        sanitizedOutput = removeInvalidEnglishCharacters(sanitizedOutput);

        if (sanitizedOutput == null || sanitizedOutput.isBlank()) {
            return null;
        }

        return sanitizedOutput.trim();
    }

    /**
     * Extracts a JSON object from a raw string response that may contain conversational text.
     * Sanitize the response string before passing it to the convertor to remove any suspicious/duplicate fields.
     * @param rawResponse The raw string response from the AI.
     * @return A string containing only the JSON object.
     */
    public String extractAndSanitizeJson(String rawResponse) {
        String extractedJson = extractJson(rawResponse);

        try {
            // Convert the json string into a map and then back into a string
            Map<String, Object> objectMap = objectMapper.readValue(extractedJson, new TypeReference<>(){});
            return objectMapper.writeValueAsString(objectMap);
        } catch (Exception e) {
            log.error("Failed to sanitize JSON, returning unsanitized json string: {}", e.getMessage());
            return extractedJson;
        }
    }

    /**
     * Extracts a JSON object from a raw string response that may contain conversational text.
     * @param rawResponse The raw string response from the AI.
     * @return A string containing only the JSON object.
     */
    private String extractJson(String rawResponse) {
        int firstBrace = rawResponse.indexOf('{');
        if (firstBrace == -1) {
            log.warn("AI response did not contain a JSON object. Raw response: {}", rawResponse);
            return "{}";
        }
        //int lastBrace = rawResponse.lastIndexOf('}');
        int braceCount = 0;
        int lastBrace = -1;
        boolean inString = false;

        for (int i = firstBrace; i < rawResponse.length(); i++) {
            char c = rawResponse.charAt(i);

            // Handle entering/exiting strings & ignoring escaped quotes
            if (c == '"' && (i == 0 || rawResponse.charAt(i - 1) != '\\')) {
                inString = !inString;
            }

            if (!inString) {
                if (c == '{') {
                    braceCount++;
                }
                else if (c == '}') {
                    braceCount--;
                }
            }

            if (braceCount == 0) {
                lastBrace = i;
                break;
            }
        }

        if (lastBrace != -1) {
            return rawResponse.substring(firstBrace, lastBrace + 1);
        }
        log.warn("Could not find a balanced JSON object in the AI response. Raw response: {}", rawResponse);
        return "{}";
    }

    private String removeInvalidEnglishCharacters(String text) {
        if (text == null) {
            return null;
        }
        return SanitizationPattern.NON_ENGLISH_CHARS.removeFrom(text).trim();
    }

    /**
     * Attempts to fix a JsonNode based on a set of validation errors.
     *
     * @param rootNode The root JsonNode of the AI response.
     * @param errors   The set of validation errors from the schema validator.
     * @return A new, potentially fixed JsonNode. Returns the original node if no fixes were applied.
     */
    public JsonNode sanitizeJsonValidationErrors(JsonNode rootNode, Set<ValidationMessage> errors) {
        // We need a mutable copy to apply fixes.
        JsonNode mutableRoot = rootNode.deepCopy();
        boolean sanitized = false;

        for (ValidationMessage error : errors) {
            String errorMessage = error.getMessage();

            // RULE 1: If a string was found where null was expected, we set the field to null.
            if (errorMessage.contains("string found, null expected")) {
                if (setFieldToNull(mutableRoot, error.getInstanceLocation().toString())) {
                    sanitized = true;
                }
            }
            // Rule 2: If it's a regex mismatch, attempt to strip invalid characters.
            else if (error.getMessage().contains("does not match the regex pattern")) {
                if (fixValidationError(mutableRoot, error)) {
                    sanitized = true;
                }
            }
        }
        return sanitized ? mutableRoot : rootNode;
    }

    private boolean setFieldToNull(JsonNode rootNode, String jsonPointerPath) {
        try {
            String parentPath = jsonPointerPath.substring(0, jsonPointerPath.lastIndexOf('/'));
            String fieldOrIndex = jsonPointerPath.substring(jsonPointerPath.lastIndexOf('/') + 1);

            JsonNode parentNode = rootNode.at(parentPath);

            if (parentNode.isMissingNode()) {
                return false;
            }

            log.info("Hot-fixing field '{}' by setting it to null due to type mismatch.", jsonPointerPath);
            if (parentNode.isObject()) {
                ((ObjectNode) parentNode).putNull(fieldOrIndex);
            }
            else if (parentNode.isArray()) {
                ((ArrayNode) parentNode).set(Integer.parseInt(fieldOrIndex), objectMapper.getNodeFactory().nullNode());
            }
            else {
                return false; // Fallback if the JSON path is invalid
            }
            return true;
        }
        catch (Exception e) {
            log.warn("Could not apply 'setFieldToNull' hot-fix for path '{}': {}", jsonPointerPath, e.getMessage());
        }
        return false;
    }

    private boolean fixValidationError(JsonNode rootNode, ValidationMessage error) {
        String jsonPointerPath = error.getInstanceLocation().toString();
        String errorMessage = error.getMessage();

        // 1. Extract the expected regex pattern from the error message
        String patternString = extractPatternFromMessage(errorMessage);
        if (patternString == null) {
            return false;
        }

        try {
            // 2. Navigate to the parent of the invalid node.
            String parentPath = jsonPointerPath.substring(0, jsonPointerPath.lastIndexOf('/'));
            String fieldOrIndex = jsonPointerPath.substring(jsonPointerPath.lastIndexOf('/') + 1);

            JsonNode parentNode = rootNode.at(parentPath);

            if (parentNode.isMissingNode()) {
                return false;
            }

            JsonNode invalidNode = parentNode.isObject() ? parentNode.get(fieldOrIndex) : parentNode.get(Integer.parseInt(fieldOrIndex));

            // 3. Dynamically build a "removal" pattern from the schema's "allow" pattern.
            if (invalidNode != null && invalidNode.isTextual()) {
                String originalText = invalidNode.asText();
                String allowedChars = extractAllowedChars(patternString);
                if (allowedChars == null) return false;

                // Match any character NOT allowed in the set.
                Pattern removalPattern = Pattern.compile("[^" + allowedChars + "]");
                String sanitizedText = removalPattern.matcher(originalText).replaceAll("");

                // 4. If a change was made, update the node.
                if (!originalText.equals(sanitizedText)) {
                    log.debug("Hot-fixing field '{}' by enforcing regex. Original: '{}', Fixed: '{}'", jsonPointerPath, originalText, sanitizedText);
                    if (parentNode.isObject()) {
                        ((ObjectNode) parentNode).put(fieldOrIndex, sanitizedText);
                    }
                    else if (parentNode.isArray()) {
                        ((ArrayNode) parentNode).set(Integer.parseInt(fieldOrIndex), sanitizedText);
                    }
                    return true;
                }
            }
        }
        catch (Exception e) {
            log.warn("Could not apply regex hot-fix for path '{}': {}", jsonPointerPath, e.getMessage());
        }
        return false;
    }

    private String extractPatternFromMessage(String message) {
        String keyword = "the regex pattern ";
        int startIndex = message.indexOf(keyword);
        if (startIndex != -1) {
            return message.substring(startIndex + keyword.length());
        }
        return null;
    }

    private String extractAllowedChars(String schemaPattern) {
        int start = schemaPattern.indexOf('[');
        int end = schemaPattern.lastIndexOf(']');
        if (start != -1 && end != -1 && start < end) {
            // Escape characters that are special inside a regex character class `[]`
            // including the backslash itself.
            return schemaPattern.substring(start + 1, end).replace("\\", "\\\\");
        }
        log.warn("Could not extract character set from regex pattern: {}", schemaPattern);
        return null;
    }

    private boolean stripParenthical(JsonNode rootNode, String jsonPath) {
        try {
            // JsonPointer paths from networknt-schema start with $, which Jackson's JsonPointer doesn't use.
            // We convert $.field[0] to /field/0

            String jsonPointerPath = jsonPath.replace("$.", "/").replace("[", "/").replace("]", "");
            String parentPath = jsonPointerPath.substring(0, jsonPointerPath.lastIndexOf('/'));
            String fieldName = jsonPointerPath.substring(jsonPointerPath.lastIndexOf('/') + 1);

            JsonNode parentNode = rootNode.at(parentPath);
            JsonNode childNode = parentNode.get(fieldName);

            if (childNode != null && childNode.isTextual()) {
                String originalText = childNode.asText();
                String sanitizedText = SanitizationPattern.PARENTHETICAL_TEXT.removeFrom(originalText).trim();

                if (!originalText.equals(sanitizedText) && parentNode instanceof ObjectNode parentObject) {
                    log.debug("Hot-fixing field '{}'. Stripping parenthetical text.", jsonPath);
                    parentObject.put(fieldName, sanitizedText);
                    return true;
                }
            }
        }
        catch (Exception e) {
            log.warn("Could not apply 'stripParenthetical' hot-fix for path '{}': {}", jsonPath, e.getMessage());
        }
        return false;
    }

}
