package com.example.language_learning.config;

import com.example.language_learning.services.actions.ChapterGenerationActions;
import com.example.language_learning.services.contexts.ChapterGenerationContext;
import com.example.language_learning.services.states.ChapterGenerationState;
import com.example.language_learning.utils.StateMachine;
import com.example.language_learning.utils.StateMachine.Transition;
import com.example.language_learning.utils.StateMachineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StateMachineConfig {

    @Bean
    public StateMachineFactory<ChapterGenerationState, ChapterGenerationContext> chapterGenerationStateMachineFactory(ChapterGenerationActions actions) {
        List<Transition<ChapterGenerationState, ChapterGenerationContext>> transitions =
                new StateMachine.GraphBuilder<ChapterGenerationState, ChapterGenerationContext>()
                        .addTransition(ChapterGenerationState.IDLE.class, ChapterGenerationState.METADATA.class,
                                (s, c) -> true, actions::handleMetadataGeneration
                        )
                        .addTransition(ChapterGenerationState.METADATA.class, ChapterGenerationState.VOCABULARY_LESSON.class,
                                (s, c) -> true, actions::handleVocabularyGeneration
                        )
                        .addTransition(ChapterGenerationState.VOCABULARY_LESSON.class, ChapterGenerationState.GRAMMAR_LESSON.class,
                                (s, c) -> c.chapter().getChapterNumber() % 2 != 0, actions::handleGrammarGeneration
                        )
                        .addTransition(ChapterGenerationState.VOCABULARY_LESSON.class, ChapterGenerationState.CONJUGATION_LESSON.class,
                                (s, c) -> c.chapter().getChapterNumber() % 2 == 0, actions::handleConjugationGeneration
                        )
                        .addTransition(ChapterGenerationState.GRAMMAR_LESSON.class, ChapterGenerationState.PRACTICE_LESSON.class,
                                (s, c) -> true, actions::handlePracticeGeneration
                        )
                        .addTransition(ChapterGenerationState.CONJUGATION_LESSON.class, ChapterGenerationState.PRACTICE_LESSON.class,
                                (s, c) -> true, actions::handlePracticeGeneration
                        )
                        .addTransition(ChapterGenerationState.PRACTICE_LESSON.class, ChapterGenerationState.READING_LESSON.class,
                                (s, c) -> true, actions::handleReadingGeneration
                        )
                        .addTransition(ChapterGenerationState.READING_LESSON.class, ChapterGenerationState.COMPLETED.class,
                                (s, c) -> true, actions::handleCompletion)
                        .build();
        return new StateMachineFactory<>(transitions, new ChapterGenerationState.IDLE());
    }
}
