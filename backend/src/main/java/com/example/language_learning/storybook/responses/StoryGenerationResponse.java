package com.example.language_learning.storybook.responses;

import com.example.language_learning.storybook.shortstory.ShortStoryDTO;

public record StoryGenerationResponse(
    String taskId,
    ShortStoryDTO shortStory
) {}
