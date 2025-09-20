package com.example.language_learning.lessonbook.requests;

import jakarta.validation.constraints.NotBlank;


public record ChapterGenerationRequest (

    @NotBlank(message = "Language cannot be blank")
    String language,

    @NotBlank(message = "Difficulty cannot be blank")
    String difficulty,

    @NotBlank(message = "Topic cannot be blank")
    String topic
) {}