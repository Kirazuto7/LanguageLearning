package com.example.language_learning.dto.models.details;

import com.example.language_learning.dto.models.WordDetailsDTO;
import lombok.Builder;

@Builder
public record GermanWordDetailsDTO(
        String lemma,
        String gender,
        String pluralForm,
        String separablePrefix // Optional
) implements WordDetailsDTO {}