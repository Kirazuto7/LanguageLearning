package com.example.language_learning.ai.contexts;

import com.example.language_learning.storybook.requests.ShortStoryGenerationRequest;
import com.example.language_learning.storybook.shortstory.ShortStory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StoryGenerationContext {
    private final ShortStoryGenerationRequest request;
    private final String taskId;
    private final Long storyId;
    private final AtomicInteger pageCounter;
    private ShortStory shortStory;
}
