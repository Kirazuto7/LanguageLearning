package com.example.language_learning.shared.word.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record JapaneseWordDetails(
    @JsonProperty("kanji") String kanji,
    @JsonProperty("hiragana") String hiragana,
    @JsonProperty("katakana") String katakana,
    @JsonProperty("romaji") String romaji
) implements WordDetails {}
