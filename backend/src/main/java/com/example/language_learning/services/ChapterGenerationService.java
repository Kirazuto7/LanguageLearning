package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.entity.lessons.*;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.exceptions.PageGenerationException;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.responses.GenerationResponse;
import com.example.language_learning.services.contexts.GenerationContext;
import com.example.language_learning.services.states.GenerationState;
import com.example.language_learning.utils.StateMachineFactory;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterGenerationService {
    private final StateMachineFactory<GenerationState, GenerationContext> stateMachineFactory;
    private final LessonBookService lessonBookService;
    private final JobQueueService jobQueueService;
    private final ProgressService progressService;
    private final ChapterService chapterService;
    private final PageService pageService;
    private final DtoMapper dtoMapper;
    private final Duration shortDelay = Duration.ofSeconds(2);


    @Builder
    private record ChapterPreparation(String taskId, Chapter chapter, int startingPageNumber) {}

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

        // 1. Find or create the lesson book
        LessonBook book = lessonBookService.findOrCreateBook(request.language(), request.difficulty(), user);

        // 2. Determine the next chapter number
        int nextChapterNumber = book.getChapters().stream()
                .mapToInt(Chapter::getChapterNumber)
                .max()
                .orElse(0) + 1;

        // 3. Create a placeholder chapter shell. The title will be updated by the async job.
        Chapter newChapter = chapterService.createChapter(book, nextChapterNumber, "Generating...", "Generating...");

        // 4. Calculate the starting page number for the new chapter
        int lastPageNumber = pageService.getLastPageNumberForBook(book.getId());
        int nextPageNumber = lastPageNumber + 1;

        // 5. Return the preparation data
        return ChapterPreparation.builder()
                .taskId(taskId)
                .chapter(newChapter)
                .startingPageNumber(nextPageNumber)
                .build();
    }

    private void generateChapterAsync(ChapterGenerationRequest request, String taskId, Long chapterId, int startingPageNumber) {
        // Asynchronously start the page(s) generation for the chapter generation subscription
        Runnable chapterGenerationJob = () -> {
            progressService.sendUpdate(taskId, 10, "Generating chapter outline...");

            try {
                Thread.sleep(shortDelay.toMillis());
                Chapter chapter = chapterService.getChapter(chapterId)
                        .orElseThrow(() -> new RuntimeException("Chapter not found for async generation: " + chapterId));

                GenerationContext context = new GenerationContext(request, taskId, chapter, new AtomicInteger(startingPageNumber));

                var sm = stateMachineFactory.createInstance();

                sm.runToCompletion(context)
                    .onCompletion(GenerationState.COMPLETED.class, completed ->
                        log.info("Chapter generation process completed successfully.")
                    )
                    .onError(GenerationState.FAILED.class, failedState -> {
                        handleFailure(failedState, context);
                        log.error("Chapter generation process finished with an error state: {}", failedState.getClass().getSimpleName());
                    })
                    .execute();
            }
            catch (Exception e) {
                log.error("Chapter generation failed for task {}: {}", taskId, e.getMessage());
                progressService.sendError(taskId, e);
            }
        };

        jobQueueService.submitJob(chapterGenerationJob);
    }

    private void handleFailure(GenerationState.FAILED failedState, GenerationContext context) {
        Exception error = new PageGenerationException(failedState.reason());
        log.error("Chapter generation failed for task {}: {}", context.taskId(), error.getMessage(), error);
        progressService.sendError(context.taskId(), error);
    }
}
