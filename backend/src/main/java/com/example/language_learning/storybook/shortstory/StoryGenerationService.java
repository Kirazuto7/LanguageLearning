package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.ai.inputs.StoryPrepInput;
import com.example.language_learning.ai.outputs.StoryPrepOutput;
import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.shared.utils.SyncWorkflow;
import com.example.language_learning.storybook.requests.ShortStoryGenerationRequest;
import com.example.language_learning.storybook.responses.StoryGenerationResponse;
import com.example.language_learning.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryGenerationService {

    private final SyncWorkflow<StoryPrepInput, StoryPrepOutput> storyPrepWorkflow;
    private final DtoMapper dtoMapper;

    @Transactional
    public StoryGenerationResponse initiateShortStoryGeneration(ShortStoryGenerationRequest request, User user) {
        StoryPrepInput input = new StoryPrepInput(request, user);
        StoryPrepOutput output = new StoryPrepOutput();
        storyPrepWorkflow.execute(input, output);

        return StoryGenerationResponse.builder()
                .taskId(output.getTaskId())
                .shortStory(dtoMapper.toDto(output.getShortStory()))
                .build();
    }
}
