package com.example.language_learning.dto.api;

import java.util.Map;

/**
 * Represents the simplified JSON structure for a vocabulary item from the AI.
 */
public record AIVocabularyItemDTO(
    String englishTranslation,
    String nativeWord,
    String phoneticSpelling,
    Map<String, Object> details
) {
}