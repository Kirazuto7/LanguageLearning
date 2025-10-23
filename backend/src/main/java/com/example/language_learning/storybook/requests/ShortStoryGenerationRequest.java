package com.example.language_learning.storybook.requests;

public record ShortStoryGenerationRequest(
    String language,
    String difficulty,
    String topic,
    String genre
) {}
