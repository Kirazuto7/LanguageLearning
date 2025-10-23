package com.example.language_learning.ai.contexts;

import com.example.language_learning.storybook.requests.ShortStoryGenerationRequest;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StoryGenerationContext {
    private final ShortStoryGenerationRequest request;
    private final String taskId;
    private final Long storyId;
    private final User user;
    private ShortStory shortStory;
}
