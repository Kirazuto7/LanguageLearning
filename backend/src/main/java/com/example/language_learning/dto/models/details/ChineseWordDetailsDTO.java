package com.example.language_learning.dto.models.details;

import com.example.language_learning.dto.models.WordDetailsDTO;
import lombok.Builder;

@Builder
public record ChineseWordDetailsDTO(
        String simplified,
        String traditional,
        String pinyin,
        String toneNumber
) implements WordDetailsDTO {}