package com.example.language_learning.shared.word.dtos;

import lombok.Builder;

@Builder
public record SpanishWordDetailsDTO(
        String lemma,
        String gender,
        String pluralForm
) implements WordDetailsDTO {}