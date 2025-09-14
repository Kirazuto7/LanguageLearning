package com.example.language_learning.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AITranslationResponse(
    @JsonProperty("translated_text") String translatedText
) {}
