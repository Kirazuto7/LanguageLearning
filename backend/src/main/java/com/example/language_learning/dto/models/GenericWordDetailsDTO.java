package com.example.language_learning.dto.models;

import lombok.Builder;

@Builder
public record GenericWordDetailsDTO(
    String nativeWord,
    String phoneticSpelling
) implements WordDetailsDTO {}
