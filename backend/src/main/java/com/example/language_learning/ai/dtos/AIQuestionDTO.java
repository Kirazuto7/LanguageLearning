package com.example.language_learning.ai.dtos;

import com.example.language_learning.mapper.util.StringOrArrayToStringDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * Represents the simplified JSON structure for a question from the AI.
 */
public record AIQuestionDTO(
        String questionText,
        @JsonProperty("answer")
        @JsonDeserialize(using = StringOrArrayToStringDeserializer.class)
        String answer,
        List<String> answerChoices
) {
}