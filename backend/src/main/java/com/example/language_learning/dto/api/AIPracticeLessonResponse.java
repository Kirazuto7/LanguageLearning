package com.example.language_learning.dto.api;

import com.example.language_learning.dto.models.QuestionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the exact JSON structure for a practice lesson from the AI.
 */
public record AIPracticeLessonResponse(
    @JsonProperty("title") String title,
    @JsonProperty("instructions") String instructions,
    @JsonProperty("answerPool") List<String> answerPool,
    @JsonProperty("questions") List<QuestionDTO> questions
) { }