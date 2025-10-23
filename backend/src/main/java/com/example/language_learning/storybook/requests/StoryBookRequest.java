package com.example.language_learning.storybook.requests;

import jakarta.validation.constraints.NotBlank;

public record StoryBookRequest (
        @NotBlank(message = "Language cannot be blank")
        String language,

        @NotBlank(message = "Difficulty cannot be blank")
        String difficulty
) {}
