package com.example.language_learning.shared.utils;

import com.example.language_learning.shared.enums.SanitizationPattern;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.*;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONTokener;
import org.jsonrepairj.JsonRepair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class AIResponseSanitizer {
    private final ObjectMapper objectMapper;

    private static final LevenshteinDistance LEVENSHTEIN_DISTANCE = LevenshteinDistance.getDefaultInstance();
    private static final int FUZZY_MATCH_THRESHOLD = 2; // Allow up to 2 character differences

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

    private String removeInvalidEnglishCharacters(String text) {
        if (text == null) {
            return null;
        }
        return SanitizationPattern.NON_ENGLISH_CHARS.removeFrom(text).trim();
    }

    /**
     * The main entry point for the pre-validation cleaning of a raw AI response. This method
     * attempts to repair the entire string to fix common syntax errors, then passes the result
     * to the extraction pipeline. This prepares the JSON for schema validation.
     *
     * @param rawResponse The raw string response from the AI.
     * @return A syntactically valid, de-duplicated JSON string ready for validation.
     */
    public String repairJson(String rawResponse) {
        try {
            // 1. Use the library to repair the JSON string's syntax.
            String repairedJson = JsonRepair.repairJson(rawResponse);
            log.info("Successfully repaired JSON with json-repairj.");
            return extractJson(repairedJson);
        }
        catch (Exception e) {
            log.warn("json-repairj failed: {}. Proceeding with the raw response for extraction.", e.getMessage());
            return extractJson(rawResponse);
        }
    }

    /**
     * The primary extraction pipeline. It attempts to extract a JSON object
     * from the given string using a two-stage process.
     * 1. It first uses {@link JSONTokener}, which is robust at handling surrounding text.
     * 2. If that fails, it falls back to a manual, brace-counting extraction method.
     * After extraction, it performs a final de-duplication pass to remove duplicate keys.
     *
     * @param jsonString The (potentially repaired) string to extract JSON from.
     * @return A de-duplicated JSON string, or the raw extracted string if final parsing fails.
     */
    private String extractJson(String jsonString) {
        String extractedJson;
        try {
            // 1. Use the library to extract the JSON string's syntax.
            Object json = new JSONTokener(jsonString).nextValue();
            extractedJson = json.toString();
        }
        catch (Exception e) {
            log.warn("JSON repair library failed. Falling back to legacy extraction method. Error: {}", e.getMessage());
            return extractJsonFallback(jsonString);
        }

        try {
            // 2. Perform the de-duplication step by reading into a generic Object
            // This handles both maps and list inputs before writing it back to a string. It also implicitly removes duplicate keys.
            Object jsonObject = objectMapper.readValue(extractedJson, new TypeReference<>() {});
            return objectMapper.writeValueAsString(jsonObject);
        }
        catch (Exception e) {
            log.error("Failed to de-duplicate JSON or parse the final extracted JSON string, Returning the raw extracted (but not de-duplicated) string: {}", e.getMessage());
            return extractedJson;
        }
    }

    /**
     * Attempts to fix a JsonNode based on a set of validation errors. This is the main
     * "hot-fix" method that applies heuristics to correct common structural and data type
     * errors based on the schema validation feedback.
     *
     * @param rootNode The root JsonNode of the AI response.
     * @param errors   The set of validation errors from the schema validator.
     * @return A new, potentially fixed JsonNode. Returns the original node if no fixes were applied.
     */
    public JsonNode sanitizeJsonValidationErrors(JsonNode rootNode, Set<ValidationMessage> errors, JsonSchema schema) {
        // We need a mutable copy to apply fixes.
        JsonNode mutableRoot = rootNode.deepCopy();

        // 1. Fix all structural errors first (e.g., malformed property names).
        boolean sanitizedKeys = fixAllMalformedKeys(mutableRoot, errors);

        // If any keys were fixed, we must re-validate to get an accurate set of errors
        Set<ValidationMessage> currentErrors = sanitizedKeys ? schema.validate(mutableRoot) : errors;

        if (currentErrors.isEmpty()) {
            return mutableRoot;
        }

        // 2. Fix value-based errors
        boolean sanitizedValues = false;

        for (ValidationMessage error : errors) {
            // RULE 1: If the error is a 'type' mismatch, attempt to coerce or nullify the value.
            if ("type".equals(error.getMessageKey())) {
                if (coerceOrNullField(mutableRoot, error, schema)) {
                    sanitizedValues = true;
                }
            }
            // RULE 2: If it's a 'pattern' mismatch, attempt to strip invalid characters.
            else if ("pattern".equals(error.getMessageKey())) {
                if (fixValidationError(mutableRoot, error, schema)) {
                    sanitizedValues = true;
                }
            }
        }
        return (sanitizedKeys || sanitizedValues) ? mutableRoot : rootNode;
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
                sanitizedText = removeTrailingPunctuatedParentheses(sanitizedText);

                // If the sanitization results in a blank string or only punctuation,
                // it's better to treat it as missing data.
                if (sanitizedText.isBlank() || sanitizedText.matches("^[\\p{Punct}\\s]+$")) {
                    log.warn("Regex hot-fix for '{}' resulted in a blank string or punctuation-only string. Checking if field is nullable...", jsonPointerPath);

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


    /**
     * Iterates through all "required property not found" errors and attempts to fix them
     * by renaming malformed keys.
     *
     * @param rootNode The root JsonNode to modify.
     * @param errors   The initial set of validation errors.
     * @return True if any key was successfully renamed, false otherwise.
     */
    private boolean fixAllMalformedKeys(JsonNode rootNode, Set<ValidationMessage> errors) {
        boolean wasSanitized = false;
        for (ValidationMessage error : errors) {
            if ("required".equals(error.getMessageKey())) {
                if (fixMalformedPropertyName(rootNode, error)) {
                    wasSanitized = true;
                }
            }
        }
        return wasSanitized;
    }

    private boolean fixMalformedPropertyName(JsonNode rootNode, ValidationMessage error) {
        String message = error.getMessage();
        Pattern pattern = Pattern.compile("required property '([^']*)' not found");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String requiredPropertyName = matcher.group(1);
            // Get the JSON Pointer path from the error location.
            String pointer = error.getInstanceLocation().toString();

            // The toString() method returns a path like '#/path/to/node', so we strip the leading '#'.
            if ("$".equals(pointer)) {
                pointer = "";
            }
            else if (!pointer.startsWith("#/"))
            {
                log.warn("Cannot fix malformed key for an invalid JSON Pointer path: {}", pointer);
                return false;
            }
            else {
                pointer = pointer.replaceFirst("^#", "");
            }

            JsonNode parentNode = rootNode.at(pointer);

            if (parentNode.isObject()) {
                ObjectNode parentObject = (ObjectNode) parentNode;
                List<String> fieldNames = new ArrayList<>();
                parentObject.fieldNames().forEachRemaining(fieldNames::add);

                for (String fieldName : fieldNames) {
                    String sanitizedFieldName = fieldName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    String sanitizedRequiredName = requiredPropertyName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                    boolean exactMatch = sanitizedFieldName.equals(sanitizedRequiredName);
                    boolean fuzzyMatch = !exactMatch && LEVENSHTEIN_DISTANCE.apply(sanitizedFieldName, sanitizedRequiredName) <= FUZZY_MATCH_THRESHOLD;

                    if ((exactMatch || fuzzyMatch) && !fieldName.equals(requiredPropertyName)) {
                        log.info("Hot-fixing malformed key: Renaming '{}' to '{}' at path '{}'", fieldName, requiredPropertyName, error.getInstanceLocation());
                        JsonNode value = parentObject.remove(fieldName);
                        parentObject.set(requiredPropertyName, value);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *  Removes trailing parenthetical expressions that only contain whitespace and punctuation.
     *       * For example, "등산로를 걸어요. (    .)" becomes "등산로를 걸어요.".
     *       *
     *       * @param text The text to sanitize.
     *       * @return The sanitized text.
     */
     private String removeTrailingPunctuatedParentheses(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
         // This regex finds parenthetical groups that contain only whitespace (\\s)
         // and punctuation (\\p{Punct}), along with any preceding whitespace.
         // It replaces all occurrences throughout the string.
         String sanitized = text.replaceAll("\\s*\\([\\s\\p{Punct}]*\\)", "");
         return sanitized.trim();
     }

    /**
     * A fallback extraction pipeline that uses a manual brace-counting method. This is called
     * when the primary {@link JSONTokener} extraction fails. It first calls {@link #jsonExtraction(String)}
     * to find the JSON substring, then performs a de-duplication pass.
     *
     * @param rawResponse The raw string response from the AI.
     * @return A de-duplicated JSON string, or the raw extracted string if de-duplication fails.
     */
    private String extractJsonFallback(String rawResponse) {
        String extractedJson = jsonExtraction(rawResponse);

        try {
            // Convert the json string into a generic object and then back into a string to handle both maps and lists
           Object jsonObject = objectMapper.readValue(extractedJson, new TypeReference<>(){});
            return objectMapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            log.error("Failed to sanitize JSON, returning unsanitized json string: {}", e.getMessage());
            return extractedJson;
        }
    }

    /**
     * Performs a low-level, manual extraction of a JSON object or array from a string. This method
     * serves as a last-resort fallback. It works by finding the first '{' or '[' and then counting
     * braces/brackets to find the corresponding closing character, ignoring characters within strings.
     *
     * @param rawResponse The raw string response from the AI.
     * @return A substring containing the balanced JSON structure, or an empty object "{}" if no structure can be found.
     */
    private String jsonExtraction(String rawResponse) {
        char startChar;
        char endChar;

        // Find the first occurrence of either a '{' or a '[' to start the extraction,
        // ignoring any preceding conversational text from the AI.
        int firstBrace = rawResponse.indexOf('{');
        int firstBracket = rawResponse.indexOf('[');

        int startCharIndex;
        // Determine if the JSON starts with an object or an array
        if (firstBrace != -1 &&  (firstBracket == -1 || firstBrace < firstBracket)) {
            startCharIndex = firstBrace;
            startChar = '{';
            endChar = '}';
        }
        else if (firstBracket != -1) {
            startCharIndex = firstBracket;
            startChar = '[';
            endChar = ']';
        }
        else {
            log.warn ("AI response did not contain any meaningful characters. Raw response: {}", rawResponse);
            return "{}";
        }

        int braceCount = 0;
        int lastEnclosingChar = -1;
        boolean inString = false;

        for (int i = startCharIndex; i < rawResponse.length(); i++) {
            char c = rawResponse.charAt(i);

            // Handle entering/exiting strings & ignoring escaped quotes
            if (c == '"' && (i == 0 || rawResponse.charAt(i - 1) != '\\')) {
                inString = !inString;
            }

            if (!inString) {
                if (c == startChar) {
                    braceCount++;
                }
                else if (c == endChar) {
                    braceCount--;
                }
            }

            if (braceCount == 0) {
                lastEnclosingChar = i;
                break;
            }
        }

        if (lastEnclosingChar != -1) {
            return rawResponse.substring(startCharIndex, lastEnclosingChar + 1);
        }
        log.warn("Could not find a balanced JSON object in the AI response. Raw response: {}", rawResponse);
        return "{}";
    }

}
