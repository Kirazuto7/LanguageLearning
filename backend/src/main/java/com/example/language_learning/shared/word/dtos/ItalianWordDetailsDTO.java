package com.example.language_learning.shared.word.dtos;

import lombok.Builder;

@Builder
public record ItalianWordDetailsDTO(
        String lemma,
        String gender,
        String pluralForm
) implements WordDetailsDTO {}