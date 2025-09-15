package com.example.language_learning.ai.actions;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.ai.components.AIRequestFactory;
import com.example.language_learning.ai.dtos.*;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.dto.models.WordDetailsDTO;
import com.example.language_learning.dto.models.WordDTO;
import com.example.language_learning.dto.models.details.*;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.Page;
import com.example.language_learning.enums.PromptType;
import com.example.language_learning.mapper.AIDtoMapper;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.ai.contexts.ChapterGenerationContext;
import com.example.language_learning.ai.states.ChapterGenerationState;
import com.example.language_learning.services.ChapterService;
import com.example.language_learning.services.PageService;
import com.example.language_learning.services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains the action implementations for the Chapter Generation state machine.
 * This component is injected into the StateMachineConfig to build the transition graph,
 * breaking the circular dependency between the service and its configuration.
 */
@Component
@RequiredArgsConstructor
public class ChapterGenerationActions {

    private final AIEngine aiEngine;
    private final AIRequestFactory aiRequestFactory;
    private final ProgressService progressService;
    private final ChapterService chapterService;
    private final PageService pageService;
    private final DtoMapper dtoMapper;
    private final AIDtoMapper AIDtoMapper;
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

            AIRequest<AIChapterMetadataResponse, ChapterMetadataDTO> aiRequest = aiRequestFactory
                    .builder(AIChapterMetadataResponse.class, response -> AIDtoMapper.toChapterMetadataDTO(response, context.getRequest().topic()))
                    .promptType(PromptType.METADATA)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .build();

            ChapterMetadataDTO metadata = aiEngine.generate(aiRequest).block();

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

            AIRequest<AIVocabularyLessonResponse<?>, VocabularyLessonDTO> aiRequest = aiRequestFactory
                    .vocabularyBuilder(context.getRequest().language(), response -> AIDtoMapper.toVocabularyLessonDTO(response, context.getRequest().language()))
                    .promptType(PromptType.VOCABULARY)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("chapterTitle", metadataDto.title())
                    .param("nativeChapterTitle", metadataDto.nativeTitle())
                    .build();

            VocabularyLessonDTO lessonDto = aiEngine.generate(aiRequest).block();

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

            AIRequest<AIGrammarLessonResponse, GrammarLessonDTO> aiRequest = aiRequestFactory
                    .builder(AIGrammarLessonResponse.class, response -> AIDtoMapper.toGrammarLessonDTO(response, context.getRequest().language()))
                    .promptType(PromptType.GRAMMAR)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("vocabulary", formatVocabularyForPrompt(vocabularyLessonDTO.vocabularies()))
                    .build();

            GrammarLessonDTO lessonDto = aiEngine.generate(aiRequest).block();

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

            AIRequest<AIConjugationLessonResponse, ConjugationLessonDTO> aiRequest = aiRequestFactory
                    .builder(AIConjugationLessonResponse.class, response -> AIDtoMapper.toConjugationLessonDTO(response, context.getRequest().language()))
                    .promptType(PromptType.CONJUGATION)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("vocabulary", formatVocabularyForPrompt(vocabularyLessonDTO.vocabularies()))
                    .build();

            ConjugationLessonDTO lessonDto = aiEngine.generate(aiRequest).block();

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

            AIRequest<AIPracticeLessonResponse, PracticeLessonDTO> aiRequest = aiRequestFactory
                    .builder(AIPracticeLessonResponse.class, response -> AIDtoMapper.toPracticeLessonDTO(response, context.getRequest().language()))
                    .promptType(PromptType.PRACTICE)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("vocabulary", formatVocabularyForPrompt(vocabularyDto.vocabularies()))
                    .param("grammarConcept", concept)
                    .build();
            PracticeLessonDTO lessonDTO = aiEngine.generate(aiRequest).block();

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

            AIRequest<AIReadingComprehensionLessonResponse, ReadingComprehensionLessonDTO> aiRequest = aiRequestFactory
                    .builder(AIReadingComprehensionLessonResponse.class, response -> AIDtoMapper.toReadingComprehensionLessonDTO(response, context.getRequest().language()))
                    .promptType(PromptType.READING_COMPREHENSION)
                    .language(context.getRequest().language())
                    .param("topic", context.getRequest().topic())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("vocabulary", formatVocabularyForPrompt(currentState.vocabularyDto().vocabularies()))
                    .param("grammarConcept", concept)
                    .build();
            ReadingComprehensionLessonDTO lessonDto = aiEngine.generate(aiRequest).block();

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