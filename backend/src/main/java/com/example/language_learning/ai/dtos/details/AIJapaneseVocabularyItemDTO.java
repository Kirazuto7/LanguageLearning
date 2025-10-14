package com.example.language_learning.ai.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIJapaneseVocabularyItemDTO(
    @JsonProperty("englishTranslation") String englishTranslation,
    @JsonProperty("kanji") String kanji,
    @JsonProperty("hiragana") String hiragana,
    @JsonProperty("katakana") String katakana,
    @JsonProperty("romaji") String romaji
) {}
