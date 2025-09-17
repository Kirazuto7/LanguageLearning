package com.example.language_learning.shared.requests;

import lombok.Builder;

@Builder
public record TranslationRequest(
    String textToTranslate,
    String sourceLanguage
) {}
