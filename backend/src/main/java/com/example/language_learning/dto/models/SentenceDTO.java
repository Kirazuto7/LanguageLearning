package com.example.language_learning.dto.models;

import lombok.Builder;

@Builder
public record SentenceDTO (
    Long id,
    String text,
    String translation
) {}
