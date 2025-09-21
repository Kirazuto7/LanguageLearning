package com.example.language_learning.ai.inputs;

import com.example.language_learning.storybook.requests.ShortStoryGenerationRequest;
import com.example.language_learning.user.User;

public record StoryPrepInput(
        ShortStoryGenerationRequest request,
        User user
) {}
