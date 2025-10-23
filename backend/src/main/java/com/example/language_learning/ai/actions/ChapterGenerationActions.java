package com.example.language_learning.ai.actions;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.chapter.ChapterMetadataDTO;
import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPage;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageDTO;
import com.example.language_learning.shared.word.dtos.*;
import com.example.language_learning.ai.enums.PromptType;
import com.example.language_learning.lessonbook.chapter.lesson.dtos.*;
import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.ai.contexts.ChapterGenerationContext;
import com.example.language_learning.ai.states.ChapterGenerationState;
import com.example.language_learning.lessonbook.chapter.LessonChapterService;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageService;
import com.example.language_learning.shared.services.ProgressService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Contains the action implementations for the Chapter Generation state machine.
 * This component is injected into the StateMachineConfig to build the transition graph,
 * breaking the circular dependency between the service and its configuration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChapterGenerationActions {

    private final AIEngine aiEngine;
    private final ProgressService progressService;
    private final LessonChapterService lessonChapterService;
    private final LessonPageService lessonPageService;
    private final DtoMapper dtoMapper;
    private final Duration shortDelay = Duration.ofSeconds(2);
    private final Duration longDelay = Duration.ofSeconds(4);
    private final Random random = new Random();

    public ChapterGenerationState handleInitialGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        log.debug("Entering handleInitialGeneration for task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 10, "Generating lessonChapter outline...", context.getUser());
            Thread.sleep(shortDelay.toMillis());

            // 1. Fetch the Chapter using the ID from the context.
            LessonChapter lessonChapter = lessonChapterService.getChapter(context.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found for async generation: " + context.getChapterId()));
            log.debug("Successfully fetched chapter. ID: {}", lessonChapter.getId());

            // 2. Store the fetched Chapter back into the context for subsequent actions.
            context.setLessonChapter(lessonChapter);

            // 3. Transition to the METADATA state to trigger the next action.
            log.debug("Transitioning to METADATA state for task ID: {}", context.getTaskId());
            return ChapterGenerationState.METADATA;
        }
        catch (Exception e) {
            log.error("Error in handleInitialGeneration for task ID: {}", context.getTaskId(), e);
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleMetadataGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        log.debug("Entering handleMetadataGeneration for task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 15, "Preparing lesson data...", context.getUser());

            AIRequest<ChapterMetadataDTO> aiRequest = AIRequest.builder()
                    .responseClass(ChapterMetadataDTO.class)
                    .promptType(PromptType.LESSON_METADATA)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .withModeration(true)
                    .build();

            ChapterMetadataDTO metadata = aiEngine.generate(aiRequest).block();

            log.debug("Generated metadata: {}", metadata);
            LessonChapter lessonChapter = context.getLessonChapter();
            assert metadata != null;
            lessonChapter.setTitle(metadata.title());
            lessonChapter.setNativeTitle(metadata.nativeTitle());
            lessonChapterService.saveChapter(lessonChapter);
            log.debug("Updated chapter metadata and saved. Transitioning to VOCABULARY_LESSON state for task ID: {}", context.getTaskId());
            return ChapterGenerationState.VOCABULARY_LESSON(metadata);
        }
        catch (Exception e) {
            log.error("Error in handleMetadataGeneration for task ID: {}", context.getTaskId(), e);
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleVocabularyGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        log.debug("Entering handleVocabularyGeneration for task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 30, "Creating vocabulary lesson...", context.getUser());
            Thread.sleep(shortDelay.toMillis());

            // 1. Cast to VOCABULARY_LESSON to get the required input data.
            ChapterMetadataDTO metadataDto = ((ChapterGenerationState.VOCABULARY_LESSON) fromState).metadataDto();

            AIRequest<VocabularyLessonDTO> aiRequest = AIRequest.builder()
                    .responseClass(VocabularyLessonDTO.class)
                    .promptType(PromptType.VOCABULARY_LESSON)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("chapterTitle", metadataDto.title())
                    .param("nativeChapterTitle", metadataDto.nativeTitle())
                    .build();

            VocabularyLessonDTO lessonDto = aiEngine.generate(aiRequest).block();
            log.debug("Generated vocabulary lesson DTO: {}", lessonDto);

            Lesson newLesson = dtoMapper.toEntity(lessonDto);
            context.getLessonsToPersist().add(newLesson);

            progressService.sendUpdate(context.getTaskId(), 40, "Vocabulary created.", context.getUser());
            Thread.sleep(longDelay.toMillis());

            // 3. Randomly decide the next state to provide variety and avoid predictable, brittle patterns.
            if (random.nextBoolean()) {
                return ChapterGenerationState.GRAMMAR_LESSON(lessonDto);
            }
            else {
                return ChapterGenerationState.CONJUGATION_LESSON(lessonDto);
            }
        }
        catch (Exception e) {
            log.error("Error in handleVocabularyGeneration for task ID: {}", context.getTaskId(), e);
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleGrammarGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        log.debug("Entering handleGrammarGeneration for task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 45, "Explaining grammar rules...", context.getUser());
            Thread.sleep(shortDelay.toMillis());

            // 1. Get the required vocabulary data from the current GRAMMAR_LESSON state.
            VocabularyLessonDTO vocabularyLessonDTO = ((ChapterGenerationState.GRAMMAR_LESSON) fromState).vocabularyDto();

            AIRequest<GrammarLessonDTO> aiRequest = AIRequest.builder()
                    .responseClass(GrammarLessonDTO.class)
                    .promptType(PromptType.GRAMMAR_LESSON)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("vocabulary", formatVocabularyForPrompt(vocabularyLessonDTO.vocabularies()))
                    .build();

            GrammarLessonDTO lessonDto = aiEngine.generate(aiRequest).block();
            log.debug("Generated grammar lesson DTO: {}", lessonDto);
            Lesson newLesson = dtoMapper.toEntity(lessonDto);
            context.getLessonsToPersist().add(newLesson);

            progressService.sendUpdate(context.getTaskId(), 55, "Grammar rules explained.", context.getUser());
            Thread.sleep(longDelay.toMillis());

            // 2. Return the next state, PRACTICE_LESSON, with all the data it needs.
            return ChapterGenerationState.PRACTICE_LESSON(vocabularyLessonDTO, lessonDto);
        }
        catch (Exception e) {
            log.error("Error in handleGrammarGeneration for task ID: {}", context.getTaskId(), e);
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleConjugationGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        log.debug("Entering handleConjugationGeneration for task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 45, "Explaining conjugation rules...", context.getUser());
            Thread.sleep(shortDelay.toMillis());

            // 1. Get the required vocabulary data from the current CONJUGATION_LESSON state.
            VocabularyLessonDTO vocabularyLessonDTO = ((ChapterGenerationState.CONJUGATION_LESSON) fromState).vocabularyDto();

            AIRequest<ConjugationLessonDTO> aiRequest = AIRequest.builder()
                    .responseClass(ConjugationLessonDTO.class)
                    .promptType(PromptType.CONJUGATION_LESSON)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("vocabulary", formatVocabularyForPrompt(vocabularyLessonDTO.vocabularies()))
                    .build();

            ConjugationLessonDTO lessonDto = aiEngine.generate(aiRequest).block();
            log.debug("Generated conjugation lesson DTO: {}", lessonDto);
            Lesson newLesson = dtoMapper.toEntity(lessonDto);
            context.getLessonsToPersist().add(newLesson);

            progressService.sendUpdate(context.getTaskId(), 55, "Conjugation rules explained.", context.getUser());
            Thread.sleep(longDelay.toMillis());

            // 2. Return the next state, PRACTICE_LESSON, with all the data it needs.
            return ChapterGenerationState.PRACTICE_LESSON(vocabularyLessonDTO, lessonDto);
        }
        catch (Exception e) {
            log.error("Error in handleConjugationGeneration for task ID: {}", context.getTaskId(), e);
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handlePracticeGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        log.debug("Entering handlePracticeGeneration for task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 60, "Building practice exercises...", context.getUser());
            Thread.sleep(shortDelay.toMillis());

            // 1. Get the required data from the current PRACTICE_LESSON state.
            ChapterGenerationState.PRACTICE_LESSON currentState = (ChapterGenerationState.PRACTICE_LESSON) fromState;
            VocabularyLessonDTO vocabularyDto = currentState.vocabularyDto();
            LessonDTO specificLesson = currentState.specificLesson();

            String concept;
            if (specificLesson instanceof  GrammarLessonDTO grammarLesson) {
                concept = grammarLesson.grammarConcept();
            }
            else if (specificLesson instanceof ConjugationLessonDTO conjugationLesson) {
                concept = conjugationLesson.explanation();
            }
            else {
                throw new IllegalArgumentException("Unsupported lesson type for practice lesson generation: " + specificLesson.getClass().getName());
            }

            AIRequest<PracticeLessonDTO> aiRequest = AIRequest.builder()
                    .responseClass(PracticeLessonDTO.class)
                    .promptType(PromptType.PRACTICE_LESSON)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("vocabulary", formatVocabularyForPrompt(vocabularyDto.vocabularies()))
                    .param("grammarConcept", concept)
                    .build();
            PracticeLessonDTO lessonDTO = aiEngine.generate(aiRequest).block();
            log.debug("Generated practice lesson DTO: {}", lessonDTO);
            Lesson newLesson = dtoMapper.toEntity(lessonDTO);
            context.getLessonsToPersist().add(newLesson);

            progressService.sendUpdate(context.getTaskId(), 75, "Practice exercises built.", context.getUser());
            Thread.sleep(longDelay.toMillis());

            // 2. Return the next state, READING_LESSON, with the data it needs.
            return ChapterGenerationState.READING_LESSON(vocabularyDto, specificLesson);
        }
        catch (Exception e) {
            log.error("Error in handlePracticeGeneration for task ID: {}", context.getTaskId(), e);
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handleReadingGeneration(ChapterGenerationState fromState, ChapterGenerationContext context) {
        log.debug("Entering handleReadingGeneration for task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 80, "Writing reading passage...", context.getUser());
            Thread.sleep(shortDelay.toMillis());

            // 1. Get the required data from the current READING_LESSON state.
            ChapterGenerationState.READING_LESSON currentState = (ChapterGenerationState.READING_LESSON) fromState;

            String concept;
            if (currentState.specificLesson() instanceof  GrammarLessonDTO grammarLesson) {
                concept = grammarLesson.grammarConcept();
            }
            else if(currentState.specificLesson() instanceof ConjugationLessonDTO conjugationLesson) {
                concept = conjugationLesson.explanation();
            }
            else {
                throw new IllegalArgumentException("Unsupported lesson type for reading comprehension lesson generation: " + currentState.specificLesson().getClass().getName());
            }

            AIRequest<ReadingComprehensionLessonDTO> aiRequest = AIRequest.builder()
                    .responseClass(ReadingComprehensionLessonDTO.class)
                    .promptType(PromptType.READING_COMPREHENSION_LESSON)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("vocabulary", formatVocabularyForPrompt(currentState.vocabularyDto().vocabularies()))
                    .param("grammarConcept", concept)
                    .build();
            ReadingComprehensionLessonDTO lessonDto = aiEngine.generate(aiRequest).block();
            log.debug("Generated reading comprehension lesson DTO: {}", lessonDto);
            Lesson newLesson = dtoMapper.toEntity(lessonDto);
            context.getLessonsToPersist().add(newLesson);

            progressService.sendUpdate(context.getTaskId(), 90, "Reading passage complete.", context.getUser());
            Thread.sleep(longDelay.toMillis());

            // 2. Return the next state, PERSIST_PAGES, with the data it needs.
            return ChapterGenerationState.PERSIST_PAGES;
        }
        catch (Exception e) {
            log.error("Error in handleReadingGeneration for task ID: {}", context.getTaskId(), e);
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    public ChapterGenerationState handlePersistPages(ChapterGenerationState fromState, ChapterGenerationContext context) {
        log.debug("Entering handlePersistPages for task ID: {}", context.getTaskId());

        try {
            List<Lesson> lessons = context.getLessonsToPersist();
            if (lessons.isEmpty()) {
                log.warn("No lessons were generated for task ID: {}. Nothing to persist.", context.getTaskId());
                return ChapterGenerationState.FAILED("No lessons were generated.");
            }

            lessonPageService.batchCreateAndPersistPages(context.getLessonChapter(), lessons);
            log.info("Successfully batch-persisted {} pages for chapter ID: {}", lessons.size(), context.getLessonChapter().getId());

            progressService.sendUpdate(context.getTaskId(), 100, "Chapter saved successfully!", context.getUser());
            return ChapterGenerationState.COMPLETED;
        }
        catch (Exception e) {
            log.error("Error in handlePersistPages for task ID: {}", context.getTaskId(), e);
            return ChapterGenerationState.FAILED(e.getMessage());
        }
    }

    private String formatVocabularyForPrompt(List<WordDTO> vocabularies) {
        if (vocabularies == null || vocabularies.isEmpty()) {
            return "No specific vocabulary provided.";
        }
        return vocabularies.stream()
                .map(wordDto -> {
                    WordDetailsDTO details = wordDto.details();
                    if (details == null) return "";

                    return switch (details) {
                        case JapaneseWordDetailsDTO j -> {
                            if (j.kanji() != null && !j.kanji().isBlank()) yield j.kanji();
                            if (j.hiragana() != null && !j.hiragana().isBlank()) yield j.hiragana();
                            yield j.katakana();
                        }
                        case KoreanWordDetailsDTO k -> k.hangul();
                        case ChineseWordDetailsDTO c -> c.simplified();
                        case ThaiWordDetailsDTO t -> t.thaiScript();
                        case ItalianWordDetailsDTO i -> i.lemma();
                        case SpanishWordDetailsDTO s -> s.lemma();
                        case FrenchWordDetailsDTO f -> f.lemma();
                        case GermanWordDetailsDTO g -> g.lemma();
                        default -> "";
                    };
                })
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(", "));
    }
}