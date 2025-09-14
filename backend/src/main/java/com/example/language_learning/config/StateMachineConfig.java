package com.example.language_learning.config;

import com.example.language_learning.services.actions.ChapterGenerationActions;
import com.example.language_learning.services.contexts.ChapterGenerationContext;
import com.example.language_learning.services.states.ChapterGenerationState;
import com.example.language_learning.utils.StateMachine;
import com.example.language_learning.utils.StateMachineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
                        .build();
        return new StateMachineFactory<>(actionMap, ChapterGenerationState.INITIAL);
    }
}
