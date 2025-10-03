package com.example.language_learning.ai.outputs;

import com.example.language_learning.storybook.StoryBook;
import com.example.language_learning.storybook.shortstory.ShortStory;
import lombok.Data;

@Data
public class StoryPrepOutput {
    private String taskId;
    private StoryBook storyBook;
    private ShortStory shortStory;
}
