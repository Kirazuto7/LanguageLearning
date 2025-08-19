package com.example.language_learning.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record ChapterGenerationRequest (

    @NotBlank(message = "Language cannot be blank")
    String language,

    @NotBlank(message = "Difficulty cannot be blank")
    String difficulty,

    @NotBlank(message = "Topic cannot be blank")
    String topic,

    @NotNull(message = "User ID cannot be null")
    Long userId
) {}