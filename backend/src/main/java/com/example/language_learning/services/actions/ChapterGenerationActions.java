package com.example.language_learning.services.actions;

import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.Page;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.services.AIService;
import com.example.language_learning.services.contexts.GenerationContext;
import com.example.language_learning.services.states.GenerationState;
import com.example.language_learning.services.ChapterService;
import com.example.language_learning.services.PageService;
import com.example.language_learning.services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Contains the action implementations for the Chapter Generation state machine.
 * This component is injected into the StateMachineConfig to build the transition graph,
 * breaking the circular dependency between the service and its configuration.
 */
@Component
@RequiredArgsConstructor
public class ChapterGenerationActions {

    private final AIService aiService;
    private final ProgressService progressService;
    private final ChapterService chapterService;
    private final PageService pageService;
    private final DtoMapper dtoMapper;
    private final Duration shortDelay = Duration.ofSeconds(2);
    private final Duration longDelay = Duration.ofSeconds(4);

    public GenerationState handleMetadataGeneration(GenerationState fromState, GenerationContext context) {
        progressService.sendUpdate(context.taskId(), 15, "Preparing lesson data...");
        // The first step of the job is to generate the metadata and update the chapter entity
        ChapterMetadataDTO metadata = aiService.generateChapterMetadata(context.request()).block();
        Chapter chapter = context.chapter();
        chapter.setTitle(metadata.title());
        chapter.setNativeTitle(metadata.nativeTitle());
        chapterService.saveChapter(chapter);
        return new GenerationState.METADATA(metadata);
    }

    public GenerationState handleVocabularyGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 25, "Creating vocabulary lesson...");
            Thread.sleep(shortDelay.toMillis());

            ChapterMetadataDTO metadataDto = ((GenerationState.METADATA) fromState).metadataDto();
            VocabularyLessonDTO lessonDto = aiService.generateVocabularyLesson(context.request(), metadataDto).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDto), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 40, "Vocabulary created.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return new GenerationState.VOCABULARY_LESSON(lessonDto);
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    public GenerationState handleGrammarGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 50, "Explaining grammar rules...");
            Thread.sleep(shortDelay.toMillis());

            VocabularyLessonDTO vocabularyLessonDTO = ((GenerationState.VOCABULARY_LESSON) fromState).vocabularyDto();
            GrammarLessonDTO lessonDto = aiService.generateGrammarLesson(context.request(), vocabularyLessonDTO).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDto), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 60, "Grammar rules explained.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return new GenerationState.GRAMMAR_LESSON(vocabularyLessonDTO, lessonDto);
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    public GenerationState handleConjugationGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 50, "Explaining conjugation rules...");
            Thread.sleep(shortDelay.toMillis());

            VocabularyLessonDTO vocabularyLessonDTO = ((GenerationState.VOCABULARY_LESSON) fromState).vocabularyDto();
            ConjugationLessonDTO lessonDto = aiService.generateConjugationLesson(context.request(), vocabularyLessonDTO).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDto), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 60, "Conjugation rules explained.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return new GenerationState.CONJUGATION_LESSON(vocabularyLessonDTO, lessonDto);
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    public GenerationState handlePracticeGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 75, "Building practice exercises...");
            Thread.sleep(shortDelay.toMillis());

            VocabularyLessonDTO vocabularyDto;
            LessonDTO specificLesson;

            if (fromState instanceof GenerationState.GRAMMAR_LESSON grammarState) {
                vocabularyDto = grammarState.vocabularyDto();
                specificLesson = grammarState.grammarLessonDto();
            }
            else if (fromState instanceof GenerationState.CONJUGATION_LESSON conjugationState) {
                vocabularyDto = conjugationState.vocabularyDto();
                specificLesson = conjugationState.conjugationLessonDto();
            }
            else {
                throw new IllegalStateException("Invalid state for practice generation: " + fromState.getClass().getSimpleName());
            }

            PracticeLessonDTO lessonDTO = aiService.generatePracticeLesson(context.request(), vocabularyDto, specificLesson).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDTO), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 85, "Practice exercises built.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return new GenerationState.PRACTICE_LESSON(vocabularyDto, specificLesson);
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    public GenerationState handleReadingGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 90, "Writing reading passage...");
            Thread.sleep(shortDelay.toMillis());

            GenerationState.PRACTICE_LESSON currentState = (GenerationState.PRACTICE_LESSON) fromState;
            ReadingComprehensionLessonDTO lessonDto = aiService.generateReadingComprehensionLesson(context.request(), currentState.vocabularyDto(), currentState.specificLesson()).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDto), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 100, "Reading passage complete.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return new GenerationState.READING_LESSON(currentState.vocabularyDto(), currentState.specificLesson());
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    public GenerationState handleCompletion(GenerationState fromState, GenerationContext context) {
        progressService.sendCompletion(context.taskId(), "Chapter generation complete.");
        return new GenerationState.COMPLETED();
    }
}