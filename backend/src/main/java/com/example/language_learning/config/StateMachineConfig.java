package com.example.language_learning.config;

import com.example.language_learning.services.actions.ChapterGenerationActions;
import com.example.language_learning.services.contexts.GenerationContext;
import com.example.language_learning.services.states.GenerationState;
import com.example.language_learning.utils.StateMachine;
import com.example.language_learning.utils.StateMachine.Transition;
import com.example.language_learning.utils.StateMachineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StateMachineConfig {

    @Bean
    public StateMachineFactory<GenerationState, GenerationContext> chapterGenerationStateMachineFactory(ChapterGenerationActions actions) {
        List<Transition<GenerationState, GenerationContext>> transitions =
                new StateMachine.GraphBuilder<GenerationState, GenerationContext>()
                        .addTransition(GenerationState.IDLE.class, GenerationState.METADATA.class,
                                (s, c) -> true, actions::handleMetadataGeneration
                        )
                        .addTransition(GenerationState.METADATA.class, GenerationState.VOCABULARY_LESSON.class,
                                (s, c) -> true, actions::handleVocabularyGeneration
                        )
                        .addTransition(GenerationState.VOCABULARY_LESSON.class, GenerationState.GRAMMAR_LESSON.class,
                                (s, c) -> c.chapter().getChapterNumber() % 2 != 0, actions::handleGrammarGeneration
                        )
                        .addTransition(GenerationState.VOCABULARY_LESSON.class, GenerationState.CONJUGATION_LESSON.class,
                                (s, c) -> c.chapter().getChapterNumber() % 2 == 0, actions::handleConjugationGeneration
                        )
                        .addTransition(GenerationState.GRAMMAR_LESSON.class, GenerationState.PRACTICE_LESSON.class,
                                (s, c) -> true, actions::handlePracticeGeneration
                        )
                        .addTransition(GenerationState.CONJUGATION_LESSON.class, GenerationState.PRACTICE_LESSON.class,
                                (s, c) -> true, actions::handlePracticeGeneration
                        )
                        .addTransition(GenerationState.PRACTICE_LESSON.class, GenerationState.READING_LESSON.class,
                                (s, c) -> true, actions::handleReadingGeneration
                        )
                        .addTransition(GenerationState.READING_LESSON.class, GenerationState.COMPLETED.class,
                                (s, c) -> true, actions::handleCompletion)
                        .build();
        return new StateMachineFactory<>(transitions, new GenerationState.IDLE());
    }
}
