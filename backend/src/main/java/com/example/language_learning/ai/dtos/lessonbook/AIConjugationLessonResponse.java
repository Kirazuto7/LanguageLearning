package com.example.language_learning.ai.dtos.lessonbook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AIConjugationLessonResponse(
        @JsonProperty("title") String title,
        @JsonProperty("conjugation_rule_name") String conjugationRuleName,
        @JsonProperty("explanation") String explanation,
        @JsonProperty("conjugated_words") List<AIConjugationExampleDTO> conjugatedWords
) {
}