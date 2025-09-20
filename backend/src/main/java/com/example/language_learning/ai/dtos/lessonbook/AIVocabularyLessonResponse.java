package com.example.language_learning.ai.dtos.lessonbook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the exact JSON structure for a vocabulary lesson from the AI.
 */
public record AIVocabularyLessonResponse<T>(
    @JsonProperty("title") String title,
    @JsonProperty("vocabularies") List<T> vocabularies
) { }