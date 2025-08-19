package com.example.language_learning.dto.api;

import java.util.List;

/**
 * Represents the simplified JSON structure for a question from the AI.
 */
public record AIQuestionDTO(
        String questionText,
        String answer,
        List<String> options
) {
}