package com.example.language_learning.shared.responses;

import lombok.Builder;

@Builder
public record TranslationResponse(
    String translatedText
) {}
