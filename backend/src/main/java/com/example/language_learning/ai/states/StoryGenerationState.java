package com.example.language_learning.ai.states;

import com.example.language_learning.shared.utils.StateMachine.TerminalState;
import com.example.language_learning.storybook.shortstory.ShortStoryMetadataDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;

import java.util.List;

public sealed interface StoryGenerationState {
    record INITIAL() implements StoryGenerationState {}
    record METADATA() implements StoryGenerationState {}
    record STORY_GENERATION(ShortStoryMetadataDTO metadataDto) implements StoryGenerationState {}
    record IMAGE_GENERATION(List<StoryPageDTO> storyPagesDto, int currentIndex, int currentProgress) implements StoryGenerationState {}
    record COMPLETED() implements StoryGenerationState, TerminalState {}
    record FAILED(String reason) implements StoryGenerationState, TerminalState {}

    static StoryGenerationState INITIAL = new INITIAL();
    static StoryGenerationState METADATA = new METADATA();
    static StoryGenerationState COMPLETED = new COMPLETED();

    static StoryGenerationState STORY_GENERATION(ShortStoryMetadataDTO metadataDto) {
        return new STORY_GENERATION(metadataDto);
    }

    static StoryGenerationState IMAGE_GENERATION(List<StoryPageDTO> storyPagesDto, int currentIndex, int currentProgress) {
        return new IMAGE_GENERATION(storyPagesDto, currentIndex, currentProgress);
    }

    static StoryGenerationState FAILED(String reason) {
        return new FAILED(reason);
    }
}
