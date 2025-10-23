package com.example.language_learning.shared.word.dtos;

import lombok.Builder;

@Builder
public record GermanWordDetailsDTO(
        String lemma,
        String gender,
        String pluralForm,
        String separablePrefix // Optional
) implements WordDetailsDTO {}