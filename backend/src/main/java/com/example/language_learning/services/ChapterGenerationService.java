package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.entity.lessons.*;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.Page;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.exceptions.PageGenerationException;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.responses.GenerationResponse;
import com.example.language_learning.utils.StateMachine;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterGenerationService {
    private List<StateMachine.Transition<GenerationState, GenerationContext>> transitions;
    private final LessonBookService lessonBookService;
    private final AIService aiService;
    private final ProgressService progressService;
    private final ChapterService chapterService;
    private final PageService pageService;
    private final DtoMapper dtoMapper;
    private final Duration shortDelay = Duration.ofSeconds(2);
    private final Duration longDelay = Duration.ofSeconds(4);

    // A sealed interface allows states to carry specific, type-safe data.
    public sealed interface GenerationState {
        record IDLE() implements GenerationState {}
        record VOCABULARY_LESSON(ChapterMetadataDTO metadataDto) implements GenerationState {}
        record GRAMMAR_LESSON(VocabularyLessonDTO vocabularyDto) implements GenerationState {}
        record CONJUGATION_LESSON(VocabularyLessonDTO vocabularyDto) implements GenerationState {}
        record PRACTICE_LESSON(VocabularyLessonDTO vocabularyDto, LessonDTO specificLesson) implements GenerationState {}
        record READING_LESSON(VocabularyLessonDTO vocabularyDTO, LessonDTO specificLesson) implements GenerationState {}
        record COMPLETED() implements GenerationState {}
        record FAILED(String reason) implements GenerationState {}
    }

    // The context holds the data that persists across all states of the generation process.
    private record GenerationContext(
            ChapterGenerationRequest request,
            String taskId,
            Chapter chapter,
            AtomicInteger pageCounter
    ) {}

    @PostConstruct
    public void init() {
        this.transitions = buildGraph();
    }

    @Builder
    private record ChapterPreparation(String taskId, Chapter chapter, int startingPageNumber) {
    }

    @Transactional
    public GenerationResponse generateChapter(ChapterGenerationRequest request, User user) {
        // 1. We retrieve the initial Chapter with metadata required for the initial request
        ChapterPreparation prep = prepareChapterGeneration(request, user);

        // 2. Kick off the chapter generation pipeline to create the associated lesson pages asynchronously
        //    after the current transaction successfully commit.s
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                generateChapterAsync(request, prep.taskId(), prep.chapter().getId(), prep.startingPageNumber());
            }
        });

        // 3. Return the expected generation response
        return GenerationResponse.builder()
                .taskId(prep.taskId())
                .chapter(dtoMapper.toDto(prep.chapter()))
                .build();
    }

    private ChapterPreparation prepareChapterGeneration(ChapterGenerationRequest request, User user) {
        final String taskId = UUID.randomUUID().toString();

        // 1. Find or create the lesson book.
        LessonBook book = lessonBookService.findOrCreateBook(request.language(), request.difficulty(), user);

        // 2. Generate chapter metadata from the AI service.
        ChapterMetadataDTO metadata = aiService.generateChapterMetadata(request).block();
        if (metadata == null) {
            throw new IllegalStateException("Failed to generate chapter metadata from AI service.");
        }

        // 3. Determine the next chapter number.
        int nextChapterNumber = book.getChapters().stream()
                .mapToInt(Chapter::getChapterNumber)
                .max()
                .orElse(0) + 1;

        // 4. Create the chapter shell for the new chapter
        Chapter newChapter = chapterService.createChapter(book, nextChapterNumber, metadata.title(), metadata.nativeTitle());

        // 5. Calculate the starting page number for the new chapter.
        int lastPageNumber = pageService.getLastPageNumberForBook(book.getId());
        int nextPageNumber = lastPageNumber + 1;

        // 6. Return the Chapter Generation Response & the page(s) generation pipeline will kick in
        return ChapterPreparation.builder()
                .taskId(taskId)
                .chapter(newChapter)
                .startingPageNumber(nextPageNumber)
                .build();
    }

    private void generateChapterAsync(ChapterGenerationRequest request, String taskId, Long chapterId, int startingPageNumber) {
        // Asynchronously start the page(s) generation for the chapter generation subscription
        Mono.fromRunnable(() -> {
            progressService.sendUpdate(taskId, 10, "Generating chapter outline...");

            try {
                Thread.sleep(shortDelay.toMillis());
                Chapter chapter = chapterService.getChapter(chapterId)
                        .orElseThrow(() -> new RuntimeException("Chapter not found for async generation: " + chapterId));

                GenerationContext context = new GenerationContext(request, taskId, chapter, new AtomicInteger(startingPageNumber));
                StateMachine<GenerationState, GenerationContext> sm = new StateMachine<>(transitions, new GenerationState.IDLE());

                while (!(sm.getCurrentState() instanceof GenerationState.COMPLETED) && !(sm.getCurrentState() instanceof GenerationState.FAILED)) {
                    sm.handle(context);
                }

                if (sm.getCurrentState() instanceof GenerationState.COMPLETED) {
                    log.info("Chapter generation process completed successfully.");
                }
                else if (sm.getCurrentState() instanceof GenerationState.FAILED failedState) {
                    handleFailure(failedState, context);
                    log.error("Chapter generation process finished with an error state: {}", sm.getCurrentState().getClass().getSimpleName());
                }
            }
            catch (Exception e) {
                log.error("Chapter generation failed for task {}: {}", taskId, e.getMessage());
                progressService.sendError(taskId, e);
            }
        })
        .doFinally(signalType -> log.info("Chapter generation stream for task {} finished with signal: {}", taskId, signalType))
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
    }

    // === Action Methods ===

    private GenerationState handleMetadataState(GenerationState fromState, GenerationContext context) {
        ChapterMetadataDTO metadata = ChapterMetadataDTO.builder()
                .title(context.chapter().getTitle())
                .nativeTitle(context.chapter().getNativeTitle())
                .topic(context.request().topic())
                .build();
        return new GenerationState.VOCABULARY_LESSON(metadata);
    }

    private GenerationState handleVocabularyGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 25, "Creating vocabulary lesson...");
            Thread.sleep(shortDelay.toMillis());

            ChapterMetadataDTO metadataDto = ((GenerationState.VOCABULARY_LESSON) fromState).metadataDto();
            VocabularyLessonDTO lessonDto = aiService.generateVocabularyLesson(context.request(), metadataDto).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDto), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 40, "Vocabulary created.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return context.chapter().getChapterNumber() % 2 != 0
                ? new GenerationState.GRAMMAR_LESSON(lessonDto)
                : new GenerationState.CONJUGATION_LESSON(lessonDto);
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    private GenerationState handleGrammarGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 50, "Explaining grammar rules...");
            Thread.sleep(shortDelay.toMillis());

            VocabularyLessonDTO vocabularyLessonDTO = ((GenerationState.GRAMMAR_LESSON) fromState).vocabularyDto();
            GrammarLessonDTO lessonDto = aiService.generateGrammarLesson(context.request, vocabularyLessonDTO).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDto), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 60, "Grammar rules explained.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return new GenerationState.PRACTICE_LESSON(vocabularyLessonDTO, lessonDto);
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    private GenerationState handleConjugationGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 50, "Explaining conjugation rules...");
            Thread.sleep(shortDelay.toMillis());

            VocabularyLessonDTO vocabularyLessonDTO = ((GenerationState.CONJUGATION_LESSON) fromState).vocabularyDto();
            ConjugationLessonDTO lessonDto = aiService.generateConjugationLesson(context.request, vocabularyLessonDTO).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDto), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 60, "Conjugation rules explained.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return new GenerationState.PRACTICE_LESSON(vocabularyLessonDTO, lessonDto);
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    private GenerationState handlePracticeGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 75, "Building practice exercises...");
            Thread.sleep(shortDelay.toMillis());

            GenerationState.PRACTICE_LESSON currentState = (GenerationState.PRACTICE_LESSON) fromState;
            PracticeLessonDTO lessonDTO = aiService.generatePracticeLesson(context.request(), currentState.vocabularyDto(), currentState.specificLesson()).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDTO), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 85, "Practice exercises built.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            return new GenerationState.READING_LESSON(currentState.vocabularyDto(), currentState.specificLesson());
        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    private GenerationState handleReadingGeneration(GenerationState fromState, GenerationContext context) {
        try {
            progressService.sendUpdate(context.taskId(), 90, "Writing reading passage...");
            Thread.sleep(shortDelay.toMillis());

            GenerationState.READING_LESSON currentState = (GenerationState.READING_LESSON) fromState;
            ReadingComprehensionLessonDTO lessonDto = aiService.generateReadingComprehensionLesson(context.request(), currentState.vocabularyDTO(), currentState.specificLesson()).block();
            Page page = pageService.createAndPersistPage(context.chapter(), dtoMapper.toEntity(lessonDto), context.pageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.taskId(), 100, "Reading passage complete.", dtoMapper.toDto(page));
            Thread.sleep(longDelay.toMillis());

            progressService.sendCompletion(context.taskId(), "Chapter generation complete.");
            return new GenerationState.COMPLETED();

        }
        catch (Exception e) {
            return new GenerationState.FAILED(e.getMessage());
        }
    }

    private void handleFailure(GenerationState.FAILED failedState, GenerationContext context) {
        Exception error = new PageGenerationException(failedState.reason());
        log.error("Chapter generation failed for task {}: {}", context.taskId(), error.getMessage(), error);
        progressService.sendError(context.taskId(), error);
    }

    // === State Transitions ===

    private List<StateMachine.Transition<GenerationState, GenerationContext>> buildGraph() {
        return List.of(
                new StateMachine.Transition<>(GenerationState.IDLE.class, GenerationState.VOCABULARY_LESSON.class,
                        (s, c) -> true, this::handleMetadataState
                ),
                new StateMachine.Transition<>(GenerationState.VOCABULARY_LESSON.class, GenerationState.GRAMMAR_LESSON.class,
                        (s, c) -> c.chapter().getChapterNumber() % 2 != 0, this::handleVocabularyGeneration
                ),
                new StateMachine.Transition<>(GenerationState.VOCABULARY_LESSON.class, GenerationState.CONJUGATION_LESSON.class,
                        (s, c) -> c.chapter().getChapterNumber() % 2 == 0, this::handleVocabularyGeneration
                ),
                new StateMachine.Transition<>(GenerationState.GRAMMAR_LESSON.class, GenerationState.PRACTICE_LESSON.class,
                        (s, c) -> true, this::handleGrammarGeneration
                ),
                new StateMachine.Transition<>(GenerationState.CONJUGATION_LESSON.class, GenerationState.PRACTICE_LESSON.class,
                        (s, c) -> true, this::handleConjugationGeneration
                ),
                new StateMachine.Transition<>(GenerationState.PRACTICE_LESSON.class, GenerationState.READING_LESSON.class,
                        (s, c) -> true, this::handlePracticeGeneration
                ),
                new StateMachine.Transition<>(GenerationState.READING_LESSON.class, GenerationState.COMPLETED.class,
                        (s, c) -> true, this::handleReadingGeneration
                )
        );
    }

}
