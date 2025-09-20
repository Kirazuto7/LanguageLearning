package com.example.language_learning.ai.dtos.lessonbook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the exact JSON structure for a practice lesson from the AI.
 */
public record AIPracticeLessonResponse(
    @JsonProperty("title") String title,
    @JsonProperty("instructions") String instructions,
    @JsonProperty("questions") List<AIQuestionDTO> questions
) { }