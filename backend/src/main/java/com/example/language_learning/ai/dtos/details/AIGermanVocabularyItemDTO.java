package com.example.language_learning.ai.dtos.details;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIGermanVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("lemma") String lemma,
        @JsonProperty("gender") String gender,
        @JsonProperty("pluralForm") String pluralForm,
        @JsonProperty("separablePrefix") String separablePrefix
) {}