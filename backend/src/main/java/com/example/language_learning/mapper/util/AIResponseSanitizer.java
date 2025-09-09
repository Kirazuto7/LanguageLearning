package com.example.language_learning.mapper.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class AIResponseSanitizer {
    private static final Pattern NON_ENGLISH_CHARS_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s.,?!'\"\\-;:()\\[\\]]");
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
        return NON_ENGLISH_CHARS_PATTERN.matcher(text).replaceAll("").trim();
    }
}
