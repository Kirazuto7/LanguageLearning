package com.example.language_learning.ai.dtos.lessonbook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the exact JSON structure for a reading lesson from the AI.
 */
public record AIReadingComprehensionLessonResponse(
    @JsonProperty("title") String title,
    @JsonProperty("story") String story,
    @JsonProperty("questions") List<AIQuestionDTO> questions
) { }