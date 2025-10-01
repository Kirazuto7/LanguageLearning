package com.example.language_learning.ai.actions;

import com.example.language_learning.ai.inputs.StoryPrepInput;
import com.example.language_learning.ai.outputs.StoryPrepOutput;
import com.example.language_learning.storybook.StoryBook;
import com.example.language_learning.storybook.StoryBookService;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.ShortStoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoryPrepActions {
    private final StoryBookService storyBookService;
    private final ShortStoryService shortStoryService;

    public void generateTaskId(StoryPrepInput input, StoryPrepOutput output) {
        String taskId = UUID.randomUUID().toString();
        log.info("Generated new story task ID: {}", taskId);
        output.setTaskId(taskId);
    }

    public void findOrCreateBook(StoryPrepInput input, StoryPrepOutput output) {
        log.info("Finding or creating story book for language: {}, difficulty: {}", input.request().language(), input.request().difficulty());
        StoryBook storyBook = storyBookService.findOrCreateBook(input.request().language(), input.request().difficulty(), input.user());
        log.info("Story book found/created with ID: {}", storyBook.getId());
        output.setStoryBook(storyBook);
    }

    public void createInitialShortStory(StoryPrepInput input, StoryPrepOutput output) {
        log.info("Creating initial short story for book ID: {}", output.getStoryBook().getId());

        String topic = input.request().topic() != null ? input.request().topic() : "Surprise Me";

        // Chapter number is now handled by the client based on order, so we create a shell with placeholder titles.
        ShortStory shortStory = shortStoryService.createShortStory(output.getStoryBook(), "Generating...", "Generating...", topic, input.request().genre());
        log.info("Created new ShortStory shell. Is it null? {}. ID: {}", shortStory == null, shortStory != null ? shortStory.getId() : "N/A");
        output.setShortStory(shortStory);
    }

}
