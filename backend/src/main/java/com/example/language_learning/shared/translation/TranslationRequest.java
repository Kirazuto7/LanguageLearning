package com.example.language_learning.shared.translation;

import lombok.Builder;

@Builder
public record TranslationRequest(
    String textToTranslate,
    String sourceLanguage
) {}
