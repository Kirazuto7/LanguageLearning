package com.example.language_learning.dto.models.details;

import com.example.language_learning.dto.models.WordDetailsDTO;
import lombok.Builder;

@Builder
public record KoreanWordDetailsDTO(
        String hangul,
        String hanja, // Optional
        String romaja
) implements WordDetailsDTO {}