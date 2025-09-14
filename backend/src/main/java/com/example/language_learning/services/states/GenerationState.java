package com.example.language_learning.services.states;

import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.utils.StateMachine;

/**
 * A sealed interface representing the distinct states of the chapter generation process.
 * Each state can carry the specific, type-safe data needed for the next step in the workflow.
 */
public sealed interface GenerationState {
    record IDLE() implements GenerationState {}
    record METADATA(ChapterMetadataDTO metadataDto) implements GenerationState {}
    record VOCABULARY_LESSON(VocabularyLessonDTO vocabularyDto) implements GenerationState {}
    record GRAMMAR_LESSON(VocabularyLessonDTO vocabularyDto, GrammarLessonDTO grammarLessonDto) implements GenerationState {}
    record CONJUGATION_LESSON(VocabularyLessonDTO vocabularyDto, ConjugationLessonDTO conjugationLessonDto) implements GenerationState {}
    record PRACTICE_LESSON(VocabularyLessonDTO vocabularyDto, LessonDTO specificLesson) implements GenerationState {}
    record READING_LESSON(VocabularyLessonDTO vocabularyDTO, LessonDTO specificLesson) implements GenerationState {}
    record COMPLETED() implements GenerationState, StateMachine.TerminalState {}
    record FAILED(String reason) implements GenerationState, StateMachine.TerminalState {}
}