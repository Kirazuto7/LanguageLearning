package com.example.language_learning.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Represents the simplified JSON structure for a vocabulary item from the AI.
 */
public record AIVocabularyItemDTO(
    @JsonProperty("englishTranslation") String englishTranslation,
    @JsonProperty("nativeWord") String nativeWord,
    @JsonProperty("phoneticSpelling") String phoneticSpelling
) {}