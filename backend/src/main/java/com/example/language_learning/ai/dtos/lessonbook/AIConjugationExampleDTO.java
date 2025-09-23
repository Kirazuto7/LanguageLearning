package com.example.language_learning.ai.dtos.lessonbook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIConjugationExampleDTO(
        @JsonProperty("infinitive") String infinitive,
        @JsonProperty("conjugated_form") String conjugatedForm,
        @JsonProperty("example_sentence") String exampleSentence,
        @JsonProperty("sentence_translation") String sentenceTranslation
) {
}