package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.ai.contexts.StoryGenerationContext;
import com.example.language_learning.ai.inputs.StoryPrepInput;
import com.example.language_learning.ai.outputs.StoryPrepOutput;
import com.example.language_learning.ai.states.StoryGenerationState;
import com.example.language_learning.shared.exceptions.StoryGenerationException;
import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.shared.services.JobQueueService;
import com.example.language_learning.shared.services.ProgressService;
import com.example.language_learning.shared.utils.StateMachineFactory;
import com.example.language_learning.shared.utils.SyncWorkflow;
import com.example.language_learning.storybook.requests.ShortStoryGenerationRequest;
import com.example.language_learning.storybook.responses.StoryGenerationResponse;
import com.example.language_learning.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryGenerationService {

    private final SyncWorkflow<StoryPrepInput, StoryPrepOutput> storyPrepWorkflow;
    private final StateMachineFactory<StoryGenerationState, StoryGenerationContext> stateMachineFactory;
    private final JobQueueService jobQueueService;
    private final ProgressService progressService;
    private final DtoMapper dtoMapper;

    @Transactional
    public StoryGenerationResponse initiateShortStoryGeneration(ShortStoryGenerationRequest request, User user) {
        StoryPrepInput input = new StoryPrepInput(request, user);
        StoryPrepOutput output = new StoryPrepOutput();
        storyPrepWorkflow.execute(input, output);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                generateStoryAsync(request, output.getTaskId(), output.getShortStory().getId());
            }
        });

        return StoryGenerationResponse.builder()
                .taskId(output.getTaskId())
                .shortStory(dtoMapper.toDto(output.getShortStory()))
                .build();
    }

    private void generateStoryAsync(ShortStoryGenerationRequest request, String taskId, Long storyId) {
        Runnable storyGenerationJob = () -> {
            try {
                StoryGenerationContext context = new StoryGenerationContext(request, taskId, storyId);
                var sm = stateMachineFactory.createInstance();
                sm.runToCompletion(context)
                        .onCompletion(StoryGenerationState.COMPLETED.class, completed -> {
                            progressService.sendCompletion(context.getTaskId(), "Story generation complete.");
                            log.info("Story generation process completed successfully for task {}.", context.getTaskId());
                        })
                        .onError(StoryGenerationState.FAILED.class, failedState -> {
                            Exception error = new StoryGenerationException(failedState.errorMessage());
                            log.error("Story generation failed for task {}: {}", context.getTaskId(), error.getMessage(), error);
                            progressService.sendError(context.getTaskId(), error);
                        })
                        .execute();
            }
            catch (Exception e) {
                log.error("Story generation failed for task {}: {}", taskId, e.getMessage());
                progressService.sendError(taskId, e);
            }
        };

        jobQueueService.submitJob(storyGenerationJob);
    }
}
