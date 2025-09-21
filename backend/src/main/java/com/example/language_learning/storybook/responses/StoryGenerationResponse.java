package com.example.language_learning.storybook.responses;

import com.example.language_learning.storybook.shortstory.ShortStoryDTO;
import lombok.Builder;

@Builder
public record StoryGenerationResponse(
    String taskId,
    ShortStoryDTO shortStory
) {}
