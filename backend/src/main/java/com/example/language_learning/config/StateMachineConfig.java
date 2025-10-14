package com.example.language_learning.config;

import com.example.language_learning.ai.actions.ChapterGenerationActions;
import com.example.language_learning.ai.actions.StoryGenerationActions;
import com.example.language_learning.ai.contexts.ChapterGenerationContext;
import com.example.language_learning.ai.contexts.StoryGenerationContext;
import com.example.language_learning.ai.states.ChapterGenerationState;
import com.example.language_learning.ai.states.StoryGenerationState;
import com.example.language_learning.shared.utils.StateMachine;
import com.example.language_learning.shared.utils.StateMachineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StateMachineConfig {

    @Bean
    public StateMachineFactory<ChapterGenerationState, ChapterGenerationContext> chapterGenerationStateMachineFactory(ChapterGenerationActions actions) {
        var actionMap =
                new StateMachine.GraphBuilder<ChapterGenerationState, ChapterGenerationContext>()
                        .addState(ChapterGenerationState.INITIAL.class, actions::handleInitialGeneration)
                        .addState(ChapterGenerationState.METADATA.class, actions::handleMetadataGeneration)
                        .addState(ChapterGenerationState.VOCABULARY_LESSON.class, actions::handleVocabularyGeneration)
                        .addState(ChapterGenerationState.GRAMMAR_LESSON.class, actions::handleGrammarGeneration)
                        .addState(ChapterGenerationState.CONJUGATION_LESSON.class, actions::handleConjugationGeneration)
                        .addState(ChapterGenerationState.PRACTICE_LESSON.class, actions::handlePracticeGeneration)
                        .addState(ChapterGenerationState.READING_LESSON.class, actions::handleReadingGeneration)
                        .addState(ChapterGenerationState.PERSIST_PAGES.class, actions::handlePersistPages)
                        .build();
        return new StateMachineFactory<>(actionMap, ChapterGenerationState.INITIAL);
    }

    @Bean
    public StateMachineFactory<StoryGenerationState, StoryGenerationContext> storyGenerationStateMachineFactory(StoryGenerationActions actions) {
        var actionMap =
                new StateMachine.GraphBuilder<StoryGenerationState, StoryGenerationContext>()
                        .addState(StoryGenerationState.INITIAL.class, actions::handleInitialGeneration)
                        .addState(StoryGenerationState.METADATA.class, actions::handleMetadataGeneration)
                        .addState(StoryGenerationState.STORY_GENERATION.class, actions::handleStoryGeneration)
                        .addState(StoryGenerationState.IMAGE_GENERATION.class, actions::handleImageGeneration)
                        .addState(StoryGenerationState.PERSIST_PAGES.class, actions::handlePersistPages)
                        .build();
        return new StateMachineFactory<>(actionMap, StoryGenerationState.INITIAL);
    }
}
