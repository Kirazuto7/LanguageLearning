package com.example.language_learning.mapper.util;

import com.example.language_learning.enums.SanitizationPattern;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.*;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.regex.PatternSyntaxException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
    public JsonNode sanitizeJsonValidationErrors(JsonNode rootNode, Set<ValidationMessage> errors, JsonSchema schema) {
        // We need a mutable copy to apply fixes.
        JsonNode mutableRoot = rootNode.deepCopy();
        boolean sanitized = false;

        for (ValidationMessage error : errors) {
            String errorMessage = error.getMessage();

            // RULE 1: If the error is a 'type' mismatch, attempt to coerce or nullify the value.
            if ("type".equals(error.getMessageKey())) {
                if (coerceOrNullField(mutableRoot, error, schema)) {
                    sanitized = true;
                }
            }
            // RULE 2: If it's a 'pattern' mismatch, attempt to strip invalid characters.
            else if ("pattern".equals(error.getMessageKey())) {
                if (fixValidationError(mutableRoot, error, schema)) {
                    sanitized = true;
                }
            }
        }
        return sanitized ? mutableRoot : rootNode;
    }

    private boolean coerceOrNullField(JsonNode rootNode, ValidationMessage error, JsonSchema schema)  {
        String jsonPointerPath = "";
        try {
            jsonPointerPath = error.getInstanceLocation().toString();
            // Convert JSONPath to JSON Pointer format
            jsonPointerPath = jsonPointerPath.replace("$.", "/")
                                             .replace("][", "/")
                                             .replace("[", "/")
                                             .replace("]", "")
                                             .replace(".", "/");

            int lastSlash = jsonPointerPath.lastIndexOf('/');
            String parentPath = (lastSlash == -1) ? "" : jsonPointerPath.substring(0, lastSlash);
            String fieldOrIndex = (lastSlash == -1) ? jsonPointerPath.substring(1) : jsonPointerPath.substring(lastSlash + 1);

            JsonNode parentNode = rootNode.at(parentPath);
            if (parentNode.isMissingNode()) {
                return false;
            }

            JsonNode invalidNode = parentNode.isObject()
                    ? parentNode.get(fieldOrIndex)
                    : parentNode.get(Integer.parseInt(fieldOrIndex));

            if (invalidNode == null || invalidNode.isNull()) {
                return false;
            }

            // Look up schema node
            String schemaPointer = error.getSchemaLocation().toString();
            // The schema path can sometimes point to the keyword itself (e.g., #/properties/foo/type).
            // We need to navigate to the parent object that contains the type definition.
            if (schemaPointer.endsWith("/pattern") || schemaPointer.endsWith("/type")) {
                schemaPointer = schemaPointer.substring(0, schemaPointer.lastIndexOf('/'));
            }
            JsonNode schemaNode = schema.getSchemaNode().at(schemaPointer.substring(1)); // Remove leading '#'


            // Determine allowed types from the schema
            List<String> allowedTypes = new ArrayList<>();
            if (schemaNode.has("type")) {
                JsonNode typeNode = schemaNode.get("type");
                if (typeNode.isTextual()) {
                    allowedTypes.add(typeNode.asText());
                } else if (typeNode.isArray()) {
                    for (JsonNode t : typeNode) {
                        if (t.isTextual()) {
                            allowedTypes.add(t.asText());
                        }
                    }
                }
            }
            // Also check inside oneOf for allowed types
            if (schemaNode.has("oneOf")) {
                for (JsonNode subSchema : schemaNode.get("oneOf")) {
                    if (subSchema.has("type")) {
                        String type = subSchema.get("type").asText();
                        if (!allowedTypes.contains(type)) {
                            allowedTypes.add(type);
                        }
                    }
                }
            }

            boolean nullAllowed = allowedTypes.contains("null");

            // Try coercion
            String original = invalidNode.asText();
            JsonNode coercedNode = null;

            if (allowedTypes.contains("integer")) {
                try {
                    coercedNode = IntNode.valueOf(Integer.parseInt(original));
                } catch (NumberFormatException ignored) {}
            } else if (allowedTypes.contains("number")) {
                try {
                    coercedNode = DoubleNode.valueOf(Double.parseDouble(original));
                } catch (NumberFormatException ignored) {}
            } else if (allowedTypes.contains("boolean")) {
                if ("true".equalsIgnoreCase(original) || "false".equalsIgnoreCase(original)) {
                    coercedNode = BooleanNode.valueOf(Boolean.parseBoolean(original));
                }
            } else if (allowedTypes.contains("string")) {
                coercedNode = TextNode.valueOf(original);
            }

            if (coercedNode != null) {
                log.info("Hot-fixing field '{}' by coercing value: '{}' -> {}", jsonPointerPath, original, coercedNode);
                if (parentNode.isObject()) {
                    ((ObjectNode) parentNode).set(fieldOrIndex, coercedNode);
                } else if (parentNode.isArray()) {
                    ((ArrayNode) parentNode).set(Integer.parseInt(fieldOrIndex), coercedNode);
                }
                return true;
            }

            // If coercion fails, fallback to null if allowed
            if (nullAllowed) {
                log.info("Hot-fixing field '{}' by setting to null (coercion failed).", jsonPointerPath);
                if (parentNode.isObject()) {
                    ((ObjectNode) parentNode).putNull(fieldOrIndex);
                } else if (parentNode.isArray()) {
                    ((ArrayNode) parentNode).set(Integer.parseInt(fieldOrIndex), NullNode.getInstance());
                }
                return true;
            }

            log.warn("Cannot coerce or nullify field '{}'. Schema requires one of {} but value='{}'.",
                    jsonPointerPath, allowedTypes, original);
        }
        catch (Exception e) {
            log.warn("Could not apply 'coerceOrNullField' hot-fix for path '{}': {}", jsonPointerPath, e.getMessage());
        }
        return false;
    }

    private boolean fixValidationError(JsonNode rootNode, ValidationMessage error, JsonSchema schema) {
        String jsonPointerPath = "";

        // 1. Resolve schema node
        String schemaPath = error.getSchemaLocation().toString();
        String pointer = schemaPath.startsWith("#") ? schemaPath.substring(1) : schemaPath;
        JsonNode schemaNode = schema.getSchemaNode().at(pointer);
        if (schemaNode.isMissingNode() || !schemaNode.isTextual()) {
            log.warn("Cannot apply regex hot-fix for path '{}': schema node missing or not textual.", error.getInstanceLocation().toString());
            return false;
        }

        String patternString = schemaNode.asText();
        if (patternString.isBlank()) {
            log.warn("Cannot apply regex hot-fix for path '{}': pattern is empty.", error.getInstanceLocation().toString());
            return false;
        }

        try {
            jsonPointerPath = error.getInstanceLocation().toString();
            // Convert JSONPath to JSON Pointer format
            jsonPointerPath = jsonPointerPath
                    .replace("$.", "/")
                    .replace("][", "/")
                    .replace("[", "/")
                    .replace("]", "")
                    .replace(".", "/");

            int lastSlash = jsonPointerPath.lastIndexOf('/');
            if (lastSlash == -1) {
                return false;
            }

            String parentPath = jsonPointerPath.substring(0, lastSlash);
            String fieldOrIndex = jsonPointerPath.substring(lastSlash + 1);
            JsonNode parentNode = rootNode.at(parentPath);

            if (parentNode.isMissingNode()) {
                return false;
            }

            JsonNode invalidNode = parentNode.isObject() ? parentNode.get(fieldOrIndex) : parentNode.get(Integer.parseInt(fieldOrIndex));
            if (invalidNode != null && invalidNode.isTextual()) {
                String originalText = invalidNode.asText();

                // 2. Try to compile the regex as a per-character pattern
                String corePattern = patternString;
                // Remove start/end anchors if present
                if (corePattern.startsWith("^")) corePattern = corePattern.substring(1);
                if (corePattern.endsWith("$")) corePattern = corePattern.substring(0, corePattern.length() - 1);
                // Remove quantifiers like + or *
                corePattern = corePattern.replaceAll("[+*]$", "");

                // Compile pattern for single-character matching
                Pattern singleCharMatcher;
                try {
                    singleCharMatcher = Pattern.compile(corePattern);
                } catch (PatternSyntaxException e) {
                    log.warn("Regex pattern '{}' is too complex for hot-fix. Skipping.", patternString);
                    return false;
                }

                // 3. Sanitize text
                StringBuilder sanitizedBuilder = new StringBuilder();
                for (char c : originalText.toCharArray()) {
                    if (singleCharMatcher.matcher(String.valueOf(c)).matches()) {
                        sanitizedBuilder.append(c);
                    }
                }

                String sanitizedText = sanitizedBuilder.toString();
                if (sanitizedText.isBlank()) {
                    log.warn("Regex hot-fix for '{}' resulted in a blank string. Checking if field is nullable...", jsonPointerPath);

                    // Find the schema definition for the field itself, not just the pattern keyword.
                    String propertySchemaPointer = error.getSchemaLocation().toString();
                    if (propertySchemaPointer.endsWith("/pattern")) {
                        propertySchemaPointer = propertySchemaPointer.substring(0, propertySchemaPointer.lastIndexOf('/'));
                    }
                    JsonNode propertySchemaNode = schema.getSchemaNode().at(propertySchemaPointer.substring(1));

                    // Check if null is an allowed type for this field.
                    boolean nullAllowed = false;
                    if (propertySchemaNode.has("type")) {
                        JsonNode typeNode = propertySchemaNode.get("type");
                        if (typeNode.isTextual() && "null".equals(typeNode.asText())) {
                            nullAllowed = true;
                        } else if (typeNode.isArray()) {
                            for (JsonNode t : typeNode) {
                                if ("null".equals(t.asText(null))) {
                                    nullAllowed = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (nullAllowed) {
                        log.info("Hot-fixing field '{}' by setting to null (regex sanitization resulted in blank string).", jsonPointerPath);
                        if (parentNode.isObject()) {
                            ((ObjectNode) parentNode).putNull(fieldOrIndex);
                        } else if (parentNode.isArray()) {
                            ((ArrayNode) parentNode).set(Integer.parseInt(fieldOrIndex), NullNode.getInstance());
                        }
                        return true; // The fix was successful (by setting to null).
                    } else {
                        log.warn("Regex hot-fix for '{}' resulted in a blank string, and schema does not allow null. Skipping.", jsonPointerPath);
                        return false; // The fix failed.
                    }
                }

                if (!originalText.equals(sanitizedText)) {
                    log.info("Hot-fixing field '{}' by enforcing regex. Original: '{}', Fixed: '{}'", jsonPointerPath, originalText, sanitizedText);
                    if (parentNode.isObject()) {
                        ((ObjectNode) parentNode).put(fieldOrIndex, sanitizedText);
                    }
                    else if (parentNode.isArray()) {
                        ((ArrayNode) parentNode).set(Integer.parseInt(fieldOrIndex), new TextNode(sanitizedText));
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
