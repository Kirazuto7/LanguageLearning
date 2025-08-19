package com.example.language_learning.dto.models;


import lombok.Builder;

import java.util.Map;

@Builder
public record WordDTO (
    Long id,
    String englishTranslation,
    String language,
    String nativeWord,
    String phoneticSpelling,
    Map<String, Object> details
) {}