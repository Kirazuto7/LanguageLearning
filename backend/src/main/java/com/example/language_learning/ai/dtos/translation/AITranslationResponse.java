package com.example.language_learning.ai.dtos.translation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AITranslationResponse(
    @JsonProperty("translated_text") String translatedText
) {}
