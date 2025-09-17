package com.example.language_learning.ai.dtos.details;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIKoreanVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("hangul") String hangul,
        @JsonProperty("hanja") String hanja,
        @JsonProperty("romaja") String romaja
) {}