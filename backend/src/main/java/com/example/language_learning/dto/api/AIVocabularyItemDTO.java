package com.example.language_learning.dto.api;

/**
 * Represents the simplified JSON structure for a vocabulary item from the AI.
 */
public record AIVocabularyItemDTO(
    String type,
    String englishTranslation,
    String nativeWord,
    String phoneticSpelling
) {
}