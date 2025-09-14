package com.example.language_learning.responses;

import lombok.Builder;

@Builder
public record TranslationResponse(
    String translatedText
) {}
