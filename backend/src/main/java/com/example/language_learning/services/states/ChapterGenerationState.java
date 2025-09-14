package com.example.language_learning.services.states;

import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.utils.StateMachine;

/**
 * A sealed interface representing the distinct states of the chapter generation process.
 * Each state can carry the specific, type-safe data needed for the next step in the workflow.
 */
public sealed interface ChapterGenerationState {
    record IDLE() implements ChapterGenerationState {}
    record METADATA(ChapterMetadataDTO metadataDto) implements ChapterGenerationState {}
    record VOCABULARY_LESSON(VocabularyLessonDTO vocabularyDto) implements ChapterGenerationState {}
    record GRAMMAR_LESSON(VocabularyLessonDTO vocabularyDto, GrammarLessonDTO grammarLessonDto) implements ChapterGenerationState {}
    record CONJUGATION_LESSON(VocabularyLessonDTO vocabularyDto, ConjugationLessonDTO conjugationLessonDto) implements ChapterGenerationState {}
    record PRACTICE_LESSON(VocabularyLessonDTO vocabularyDto, LessonDTO specificLesson) implements ChapterGenerationState {}
    record READING_LESSON(VocabularyLessonDTO vocabularyDTO, LessonDTO specificLesson) implements ChapterGenerationState {}
    record COMPLETED() implements ChapterGenerationState, StateMachine.TerminalState {}
    record FAILED(String reason) implements ChapterGenerationState, StateMachine.TerminalState {}
}