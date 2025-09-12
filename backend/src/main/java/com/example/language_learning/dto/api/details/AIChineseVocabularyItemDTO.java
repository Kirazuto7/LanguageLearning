package com.example.language_learning.dto.api.details;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIChineseVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("simplified") String simplified,
        @JsonProperty("traditional") String traditional,
        @JsonProperty("pinyin") String pinyin,
        @JsonProperty("toneNumber") String toneNumber
) {}