package com.example.language_learning.services.actions;

import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.Page;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.services.AIService;
import com.example.language_learning.services.contexts.ChapterGenerationContext;
import com.example.language_learning.services.states.ChapterGenerationState;
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

    public ChapterGenerationState handleInitialGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 10, "Generating chapter outline...");
            Thread.sleep(shortDelay.toMillis());

            // 1. Fetch the Chapter using the ID from the context.
            Chapter chapter = chapterService.getChapter(context.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found for async generation: " + context.getChapterId()));

            // 2. Store the fetched Chapter back into the context for subsequent actions.
            context.setChapter(chapter);

            // 3. Transition to the METADATA state to trigger the next action.
            return ChapterGenerationState.METADATA;
        }
        catch (Exception e) {
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleMetadataGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 15, "Preparing lesson data...");
            // The first step of the job is to generate the metadata and update the chapter entity
            ChapterMetadataDTO metadata = aiService.generateChapterMetadata(context.getRequest()).block();
            Chapter chapter = context.getChapter();
            assert metadata != null;
            chapter.setTitle(metadata.title());
            chapter.setNativeTitle(metadata.nativeTitle());
            chapterService.saveChapter(chapter);
            return ChapterGenerationState.VOCABULARY_LESSON(metadata);
        }
        catch (Exception e) {
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleVocabularyGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 25, "Creating vocabulary lesson...");
            Thread.sleep(shortDelay.toMillis());

            // 1. Cast to VOCABULARY_LESSON to get the required input data.
            ChapterMetadataDTO metadataDto = ((ChapterGenerationState.VOCABULARY_LESSON) fromState).metadataDto();

            // 2. Perform the action: generate the vocabulary lesson.
            VocabularyLessonDTO lessonDto = aiService.generateVocabularyLesson(context.getRequest(), metadataDto).block();
            Page page = pageService.createAndPersistPage(context.getChapter(), dtoMapper.toEntity(lessonDto), context.getPageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.getTaskId(), 40, "Vocabulary created.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            // 3. Decide the next state based on the branching logic and return it with the required data.
            if (context.getChapter().getChapterNumber() % 2 != 0) {
                return ChapterGenerationState.GRAMMAR_LESSON(lessonDto);
            }
            else {
                return ChapterGenerationState.CONJUGATION_LESSON(lessonDto);
            }
        }
        catch (Exception e) {
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleGrammarGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 50, "Explaining grammar rules...");
            Thread.sleep(shortDelay.toMillis());

            // 1. Get the required vocabulary data from the current GRAMMAR_LESSON state.
            VocabularyLessonDTO vocabularyLessonDTO = ((ChapterGenerationState.GRAMMAR_LESSON) fromState).vocabularyDto();
            GrammarLessonDTO lessonDto = aiService.generateGrammarLesson(context.getRequest(), vocabularyLessonDTO).block();
            Page page = pageService.createAndPersistPage(context.getChapter(), dtoMapper.toEntity(lessonDto), context.getPageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.getTaskId(), 60, "Grammar rules explained.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            // 2. Return the next state, PRACTICE_LESSON, with all the data it needs.
            return ChapterGenerationState.PRACTICE_LESSON(vocabularyLessonDTO, lessonDto);
        }
        catch (Exception e) {
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleConjugationGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 50, "Explaining conjugation rules...");
            Thread.sleep(shortDelay.toMillis());

            // 1. Get the required vocabulary data from the current CONJUGATION_LESSON state.
            VocabularyLessonDTO vocabularyLessonDTO = ((ChapterGenerationState.CONJUGATION_LESSON) fromState).vocabularyDto();
            ConjugationLessonDTO lessonDto = aiService.generateConjugationLesson(context.getRequest(), vocabularyLessonDTO).block();
            Page page = pageService.createAndPersistPage(context.getChapter(), dtoMapper.toEntity(lessonDto), context.getPageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.getTaskId(), 60, "Conjugation rules explained.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            // 2. Return the next state, PRACTICE_LESSON, with all the data it needs.
            return ChapterGenerationState.PRACTICE_LESSON(vocabularyLessonDTO, lessonDto);
        }
        catch (Exception e) {
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handlePracticeGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 75, "Building practice exercises...");
            Thread.sleep(shortDelay.toMillis());

            // 1. Get the required data from the current PRACTICE_LESSON state.
            ChapterGenerationState.PRACTICE_LESSON currentState = (ChapterGenerationState.PRACTICE_LESSON) fromState;
            VocabularyLessonDTO vocabularyDto = currentState.vocabularyDto();
            LessonDTO specificLesson = currentState.specificLesson();

            PracticeLessonDTO lessonDTO = aiService.generatePracticeLesson(context.getRequest(), vocabularyDto, specificLesson).block();
            Page page = pageService.createAndPersistPage(context.getChapter(), dtoMapper.toEntity(lessonDTO), context.getPageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.getTaskId(), 85, "Practice exercises built.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            // 2. Return the next state, READING_LESSON, with the data it needs.
            return ChapterGenerationState.READING_LESSON(vocabularyDto, specificLesson);
        }
        catch (Exception e) {
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleReadingGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 90, "Writing reading passage...");
            Thread.sleep(shortDelay.toMillis());

            // 1. Get the required data from the current READING_LESSON state.
            ChapterGenerationState.READING_LESSON currentState = (ChapterGenerationState.READING_LESSON) fromState;
            ReadingComprehensionLessonDTO lessonDto = aiService.generateReadingComprehensionLesson(context.getRequest(), currentState.vocabularyDto(), currentState.specificLesson()).block();
            Page page = pageService.createAndPersistPage(context.getChapter(), dtoMapper.toEntity(lessonDto), context.getPageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.getTaskId(), 100, "Reading passage complete.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            // 2. Return the next state, COMPLETED, with the data it needs.
            return ChapterGenerationState.COMPLETED;
        }
        catch (Exception e) {
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }
}