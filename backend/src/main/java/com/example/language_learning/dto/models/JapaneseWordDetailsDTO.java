package com.example.language_learning.dto.models;

import lombok.Builder;

@Builder
public record JapaneseWordDetailsDTO(
    String kanji,
    String hiragana,
    String katakana,
    String romaji
) implements WordDetailsDTO {}
