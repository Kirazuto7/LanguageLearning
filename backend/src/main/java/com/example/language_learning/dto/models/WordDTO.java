package com.example.language_learning.dto.models;


import lombok.Builder;


@Builder
public record WordDTO (
    Long id,
    String englishTranslation,
    String language,
    WordDetailsDTO details
) {}