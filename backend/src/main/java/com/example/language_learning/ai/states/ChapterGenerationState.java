package com.example.language_learning.ai.states;

import com.example.language_learning.lessonbook.chapter.ChapterMetadataDTO;
import com.example.language_learning.lessonbook.chapter.lesson.dtos.LessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.dtos.VocabularyLessonDTO;
import com.example.language_learning.shared.utils.StateMachine.TerminalState;

/**
 * A sealed interface representing the distinct states of the lessonChapter generation process.
 * Each state can carry the specific, type-safe data needed for the next step in the workflow.
 */
public sealed interface ChapterGenerationState {
    record INITIAL() implements ChapterGenerationState {}
    record METADATA() implements ChapterGenerationState {}
    record VOCABULARY_LESSON(ChapterMetadataDTO metadataDto) implements ChapterGenerationState {}
    record GRAMMAR_LESSON(VocabularyLessonDTO vocabularyDto) implements ChapterGenerationState {}
    record CONJUGATION_LESSON(VocabularyLessonDTO vocabularyDto) implements ChapterGenerationState {}
    record PRACTICE_LESSON(VocabularyLessonDTO vocabularyDto, LessonDTO specificLesson) implements ChapterGenerationState {}
    record READING_LESSON(VocabularyLessonDTO vocabularyDto, LessonDTO specificLesson) implements ChapterGenerationState {}
    record PERSIST_PAGES() implements ChapterGenerationState {}
    record COMPLETED() implements ChapterGenerationState, TerminalState {}
    record FAILED(String reason) implements ChapterGenerationState, TerminalState {}

    static ChapterGenerationState INITIAL = new INITIAL();
    static ChapterGenerationState METADATA = new METADATA();
    static ChapterGenerationState PERSIST_PAGES = new PERSIST_PAGES();
    static ChapterGenerationState COMPLETED = new COMPLETED();

    public static ChapterGenerationState VOCABULARY_LESSON(ChapterMetadataDTO metadataDto) {
        return new VOCABULARY_LESSON(metadataDto);
    }

    public static ChapterGenerationState GRAMMAR_LESSON(VocabularyLessonDTO vocabularyDto) {
        return new GRAMMAR_LESSON(vocabularyDto);
    }

    public static ChapterGenerationState CONJUGATION_LESSON(VocabularyLessonDTO vocabularyDto) {
        return new CONJUGATION_LESSON(vocabularyDto);
    }

    public static ChapterGenerationState PRACTICE_LESSON(VocabularyLessonDTO vocabularyDto, LessonDTO specificLesson) {
        return new PRACTICE_LESSON(vocabularyDto, specificLesson);
    }

    public static ChapterGenerationState READING_LESSON(VocabularyLessonDTO vocabularyDto, LessonDTO specificLesson) {
        return new READING_LESSON(vocabularyDto, specificLesson);
    }

    public static ChapterGenerationState FAILED(String reason) {
        return new FAILED(reason);
    }
}