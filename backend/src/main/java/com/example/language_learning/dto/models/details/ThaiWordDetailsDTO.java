package com.example.language_learning.dto.models.details;

import com.example.language_learning.dto.models.WordDetailsDTO;
import lombok.Builder;

@Builder
public record ThaiWordDetailsDTO(
        String thaiScript,
        String romanization,
        String tonePattern
) implements WordDetailsDTO {}