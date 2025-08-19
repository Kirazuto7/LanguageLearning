package com.example.language_learning.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the exact JSON structure for a vocabulary lesson from the AI.
 */
public record AIVocabularyLessonResponse(
    @JsonProperty("title") String title,
    @JsonProperty("vocabularies") List<AIVocabularyItemDTO> vocabularies
) { }