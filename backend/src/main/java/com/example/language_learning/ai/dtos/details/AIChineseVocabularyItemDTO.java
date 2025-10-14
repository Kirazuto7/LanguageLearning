package com.example.language_learning.ai.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIChineseVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("simplified") String simplified,
        @JsonProperty("traditional") String traditional,
        @JsonProperty("pinyin") String pinyin,
        @JsonProperty("toneNumber") String toneNumber
) {}