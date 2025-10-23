package com.example.language_learning.ai.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIGermanVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("lemma") String lemma,
        @JsonProperty("gender") String gender,
        @JsonProperty("pluralForm") String pluralForm,
        @JsonProperty("separablePrefix") String separablePrefix
) {}