package com.example.language_learning.entity.models.details;

import com.example.language_learning.entity.models.WordDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record JapaneseWordDetails(
    @JsonProperty("kanji") String kanji,
    @JsonProperty("hiragana") String hiragana,
    @JsonProperty("katakana") String katakana,
    @JsonProperty("romaji") String romaji
) implements WordDetails {}
