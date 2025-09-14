package com.example.language_learning.requests;

import lombok.Builder;

@Builder
public record TranslationRequest(
    String textToTranslate,
    String sourceLanguage
) {}
