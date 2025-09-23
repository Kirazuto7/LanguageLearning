package com.example.language_learning.shared.word.dtos;

import lombok.Builder;

@Builder
public record KoreanWordDetailsDTO(
        String hangul,
        String hanja, // Optional
        String romaja
) implements WordDetailsDTO {}