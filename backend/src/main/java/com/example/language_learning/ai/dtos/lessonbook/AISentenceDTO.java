package com.example.language_learning.ai.dtos.lessonbook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AISentenceDTO(
    @JsonProperty("text") String text,
    @JsonProperty("translation") String translation
) {}
