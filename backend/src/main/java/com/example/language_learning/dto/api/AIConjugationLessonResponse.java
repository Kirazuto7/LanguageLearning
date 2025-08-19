package com.example.language_learning.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AIConjugationLessonResponse(
        @JsonProperty("title") String title,
        @JsonProperty("infinitive") String infinitive,
        @JsonProperty("explanation") String explanation,
        @JsonProperty("conjugation_table") List<AIConjugationExampleDTO> conjugationTable
) {
}