package com.example.language_learning.shared.word.dtos;

import lombok.Builder;

@Builder
public record ChineseWordDetailsDTO(
        String simplified,
        String traditional,
        String pinyin,
        String toneNumber
) implements WordDetailsDTO {}