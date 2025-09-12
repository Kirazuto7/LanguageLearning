package com.example.language_learning.dto.api.details;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIJapaneseVocabularyItemDTO(
    @JsonProperty("englishTranslation") String englishTranslation,
    @JsonProperty("kanji") String kanji,
    @JsonProperty("hiragana") String hiragana,
    @JsonProperty("katakana") String katakana,
    @JsonProperty("romaji") String romaji
) {}
