package com.example.language_learning.ai.states;

import com.example.language_learning.shared.utils.StateMachine.TerminalState;
import com.example.language_learning.storybook.shortstory.ShortStoryMetadataDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StoryGenerationState.INITIAL.class, name = "INITIAL"),
        @JsonSubTypes.Type(value = StoryGenerationState.METADATA.class, name = "METADATA"),
        @JsonSubTypes.Type(value = StoryGenerationState.STORY_GENERATION.class, name = "STORY_GENERATION"),
        @JsonSubTypes.Type(value = StoryGenerationState.IMAGE_GENERATION.class, name = "IMAGE_GENERATION"),
        @JsonSubTypes.Type(value = StoryGenerationState.PERSIST_PAGES.class, name = "PERSIST_PAGES"),
        @JsonSubTypes.Type(value = StoryGenerationState.COMPLETED.class, name = "COMPLETED"),
        @JsonSubTypes.Type(value = StoryGenerationState.FAILED.class, name = "FAILED")
})
public sealed interface StoryGenerationState {

    // Initial state, no data
    record INITIAL() implements StoryGenerationState {}

    // State for generating story metadata
    record METADATA() implements StoryGenerationState {}

    // State for generating the story content, holding metadata
    record STORY_GENERATION(ShortStoryMetadataDTO metadataDto) implements StoryGenerationState {}

    // State for generating all images in a batch, holding the list of lessonPages
    record IMAGE_GENERATION(List<StoryPageDTO> storyPagesDto) implements StoryGenerationState {}

    // New recursive state for persisting lessonPages one by one
    record PERSIST_PAGES(List<StoryPageDTO> storyPagesDto, int currentIndex, int currentProgress) implements StoryGenerationState {}

    // Terminal state for successful completion
    record COMPLETED() implements StoryGenerationState, TerminalState {}

    // Terminal state for failure, holding an error message
    record FAILED(String errorMessage) implements StoryGenerationState, TerminalState {}

    // --- Static Factory Methods for easy state creation ---

    static StoryGenerationState INITIAL = new INITIAL();
    static StoryGenerationState METADATA = new METADATA();
    static StoryGenerationState COMPLETED = new COMPLETED();

    static StoryGenerationState STORY_GENERATION(ShortStoryMetadataDTO metadataDto) {
        return new STORY_GENERATION(metadataDto);
    }

    static StoryGenerationState IMAGE_GENERATION(List<StoryPageDTO> storyPagesDto) {
        return new IMAGE_GENERATION(storyPagesDto);
    }

    static StoryGenerationState PERSIST_PAGES(List<StoryPageDTO> storyPagesDto) {
        // Initial entry into the persistence state
        return new PERSIST_PAGES(storyPagesDto, 0, 60);
    }

    static StoryGenerationState PERSIST_PAGES(List<StoryPageDTO> storyPagesDto, int currentIndex, int currentProgress) {
        // Recursive call for the next page
        return new PERSIST_PAGES(storyPagesDto, currentIndex, currentProgress);
    }

    static StoryGenerationState FAILED(String errorMessage) {
        return new FAILED(errorMessage);
    }
}
