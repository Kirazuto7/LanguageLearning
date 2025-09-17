package com.example.language_learning.shared.requests;

import jakarta.validation.constraints.NotBlank;


public record LessonBookRequest (
    @NotBlank(message = "Language cannot be blank")
    String language,

    @NotBlank(message = "Difficulty cannot be blank")
    String difficulty
) {}