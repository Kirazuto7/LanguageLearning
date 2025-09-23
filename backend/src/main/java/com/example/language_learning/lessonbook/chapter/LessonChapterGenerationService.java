package com.example.language_learning.lessonbook.chapter;

import com.example.language_learning.user.User;
import com.example.language_learning.shared.exceptions.PageGenerationException;
import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.lessonbook.requests.ChapterGenerationRequest;
import com.example.language_learning.lessonbook.responses.GenerationResponse;
import com.example.language_learning.ai.contexts.ChapterGenerationContext;
import com.example.language_learning.ai.inputs.ChapterPrepInput;
import com.example.language_learning.ai.outputs.ChapterPrepOutput;
import com.example.language_learning.ai.states.ChapterGenerationState;
import com.example.language_learning.shared.services.JobQueueService;
import com.example.language_learning.shared.services.ProgressService;
import com.example.language_learning.shared.utils.StateMachineFactory;
import com.example.language_learning.shared.utils.SyncWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonChapterGenerationService {
    private final SyncWorkflow<ChapterPrepInput, ChapterPrepOutput> chapterPrepWorkflow;
    private final StateMachineFactory<ChapterGenerationState, ChapterGenerationContext> stateMachineFactory;
    private final JobQueueService jobQueueService;
    private final ProgressService progressService;
    private final DtoMapper dtoMapper;


    @Transactional
    public GenerationResponse generateChapter(ChapterGenerationRequest request, User user) {
        // 1. Define the inputs and create the output container for the synchronous workflow.
        ChapterPrepInput input = new ChapterPrepInput(request, user);
        ChapterPrepOutput output = new ChapterPrepOutput();

        // 2. Execute the synchronous workflow to prepare all necessary data.
        chapterPrepWorkflow.execute(input, output);

        // 3. Kick off the chapter generation pipeline to create the associated lesson pages asynchronously
        //    after the current transaction successfully commits
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                generateChapterAsync(request, output.getTaskId(), output.getLessonChapter().getId(), output.getStartingPageNumber());
            }
        });

        // 4. Immediately return the response to the user.
        return GenerationResponse.builder()
                .taskId(output.getTaskId())
                .chapter(dtoMapper.toDto(output.getLessonChapter()))
                .build();
    }

    private void generateChapterAsync(ChapterGenerationRequest request, String taskId, Long chapterId, int startingPageNumber) {
        // Asynchronously start the page(s) generation for the chapter generation subscription
        Runnable chapterGenerationJob = () -> {
            try {
                ChapterGenerationContext context = new ChapterGenerationContext(request, taskId, chapterId, new AtomicInteger(startingPageNumber));

                var sm = stateMachineFactory.createInstance();

                sm.runToCompletion(context)
                    .onCompletion(ChapterGenerationState.COMPLETED.class, completed -> {
                        progressService.sendCompletion(context.getTaskId(), "Chapter generation complete.");
                        log.info("Chapter generation process completed successfully for task {}.", context.getTaskId());
                    })
                    .onError(ChapterGenerationState.FAILED.class, failedState -> {
                        Exception error = new PageGenerationException(failedState.reason());
                        log.error("Chapter generation failed for task {}: {}", context.getTaskId(), error.getMessage(), error);
                        progressService.sendError(context.getTaskId(), error);
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
}
