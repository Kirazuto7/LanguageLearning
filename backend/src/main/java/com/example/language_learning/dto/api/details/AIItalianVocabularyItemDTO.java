package com.example.language_learning.dto.api.details;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIItalianVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("lemma") String lemma,
        @JsonProperty("gender") String gender,
        @JsonProperty("pluralForm") String pluralForm
) {}