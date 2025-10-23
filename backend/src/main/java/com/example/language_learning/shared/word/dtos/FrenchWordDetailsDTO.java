package com.example.language_learning.shared.word.dtos;

import lombok.Builder;

@Builder
public record FrenchWordDetailsDTO(
        String lemma,
        String gender,
        String pluralForm
) implements WordDetailsDTO {}