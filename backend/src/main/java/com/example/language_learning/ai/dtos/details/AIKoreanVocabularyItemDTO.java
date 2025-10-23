package com.example.language_learning.ai.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIKoreanVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("hangul") String hangul,
        @JsonProperty("hanja") String hanja,
        @JsonProperty("romaja") String romaja
) {}