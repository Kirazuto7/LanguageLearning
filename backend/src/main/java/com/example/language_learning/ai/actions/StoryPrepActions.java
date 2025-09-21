package com.example.language_learning.ai.actions;

import com.example.language_learning.ai.inputs.StoryPrepInput;
import com.example.language_learning.ai.outputs.StoryPrepOutput;
import com.example.language_learning.storybook.StoryBook;
import com.example.language_learning.storybook.StoryBookService;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.ShortStoryService;
import com.example.language_learning.storybook.shortstory.page.StoryPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoryPrepActions {
    private final StoryBookService storyBookService;
    private final ShortStoryService shortStoryService;
    private final StoryPageService storyPageService;

    public void generateTaskId(StoryPrepInput input, StoryPrepOutput output) {
        output.setTaskId(UUID.randomUUID().toString());
    }

    public void findOrCreateBook(StoryPrepInput input, StoryPrepOutput output) {
        StoryBook storyBook = storyBookService.findOrCreateBook(input.request().language(), input.request().difficulty(), input.user());
        output.setStoryBook(storyBook);
    }

    public void createInitialShortStory(StoryPrepInput input, StoryPrepOutput output) {
        int nextStoryNumber = output.getStoryBook().getShortStories().stream()
                .mapToInt(ShortStory::getChapterNumber)
                .max()
                .orElse(0) + 1;
        ShortStory shortStory = shortStoryService.createShortStory(output.getStoryBook(), nextStoryNumber, "Generating...", "Generating...", input.request().topic(), input.request().genre());
        output.setShortStory(shortStory);
    }

    public void calculateStartingPage(StoryPrepInput input, StoryPrepOutput output) {
        int lastPageNumber = storyPageService.getLastPageForBook(output.getStoryBook().getId());
        int nextPageNumber = lastPageNumber + 1;
        output.setStartingPageNumber(nextPageNumber);
    }

}
