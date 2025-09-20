package com.example.language_learning.shared.word.dtos;

import lombok.Builder;

@Builder
public record JapaneseWordDetailsDTO(
    String kanji,
    String hiragana,
    String katakana,
    String romaji
) implements WordDetailsDTO {}
