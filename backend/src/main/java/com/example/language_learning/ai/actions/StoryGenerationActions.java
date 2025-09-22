package com.example.language_learning.ai.actions;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.ai.contexts.StoryGenerationContext;
import com.example.language_learning.ai.enums.PromptType;
import com.example.language_learning.ai.states.StoryGenerationState;
import com.example.language_learning.shared.dtos.images.GeneratedImageDTO;
import com.example.language_learning.shared.mapper.DtoMapper;
import com.example.language_learning.shared.services.ProgressService;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.ShortStoryDTO;
import com.example.language_learning.storybook.shortstory.ShortStoryMetadataDTO;
import com.example.language_learning.storybook.shortstory.ShortStoryService;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StoryGenerationActions {
    private final AIEngine aiEngine;
    private final ProgressService progressService;
    private final ShortStoryService shortStoryService;
    private final StoryPageService storyPageService;
    private final DtoMapper dtoMapper;
    private final Duration shortDelay = Duration.ofSeconds(2);
    private final Duration longDelay = Duration.ofSeconds(4);

    public StoryGenerationState handleInitialGeneration(StoryGenerationState fromState, StoryGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 10 , "Initializing story generation...");
            Thread.sleep(shortDelay.toMillis());

            ShortStory shortStory = shortStoryService.getShortStory(context.getStoryId())
                    .orElseThrow(() -> new RuntimeException("ShortStory not found for async generation: " + context.getStoryId()));

            context.setShortStory(shortStory);

            return StoryGenerationState.METADATA;
        }
        catch (Exception e) {
            log.error("Error during story generation initialization for task {}: {}", context.getTaskId(), e.getMessage(), e);
            return StoryGenerationState.FAILED(e.getMessage());
        }
    }

    public StoryGenerationState handleMetadataGeneration(StoryGenerationState fromState, StoryGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 15, "Preparing story data...");

            AIRequest<ShortStoryMetadataDTO> aiRequest = AIRequest.builder()
                    .responseClass(ShortStoryMetadataDTO.class)
                    .language(context.getRequest().language())
                    .promptType(PromptType.STORY_METADATA)
                    .param("topic", context.getShortStory().getTopic())
                    .param("genre", context.getRequest().genre())
                    .param("difficulty", context.getRequest().difficulty())
                    .build();

            ShortStoryMetadataDTO metadata = aiEngine.generate(aiRequest).block();

            ShortStory shortStory = context.getShortStory();
            assert metadata != null;
            shortStory.setTitle(metadata.title());
            shortStory.setNativeTitle(metadata.nativeTitle());
            shortStory.setTopic(metadata.topic());
            shortStory.setGenre(metadata.genre());
            shortStoryService.saveShortStory(shortStory);
            return StoryGenerationState.STORY_GENERATION(metadata);
        }
        catch (Exception e) {
            return StoryGenerationState.FAILED(e.getMessage());
        }
    }

    public StoryGenerationState handleStoryGeneration(StoryGenerationState fromState, StoryGenerationContext context) {
        try {
            progressService.sendUpdate(context.getTaskId(), 30, "Writing the story...");
            Thread.sleep(shortDelay.toMillis());

            ShortStoryMetadataDTO metadata = ((StoryGenerationState.STORY_GENERATION) fromState).metadataDto();

            AIRequest<ShortStoryDTO> aiRequest = AIRequest.builder()
                    .responseClass(ShortStoryDTO.class)
                    .promptType(PromptType.STORY_PAGES)
                    .language(context.getRequest().language())
                    .param("topic", metadata.topic())
                    .param("genre", context.getRequest().genre())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("storyTitle", metadata.title())
                    .param("nativeStoryTitle", metadata.nativeTitle())
                    .build();

            ShortStoryDTO storyDto = aiEngine.generate(aiRequest).block();
            assert storyDto != null;
            return StoryGenerationState.IMAGE_GENERATION(storyDto.storyPages(), 0, 40);
        }
        catch (Exception e) {
            return StoryGenerationState.FAILED(e.getMessage());
        }
    }

    public StoryGenerationState handleImageGeneration(StoryGenerationState fromState, StoryGenerationContext context) {
        StoryGenerationState.IMAGE_GENERATION currentState = (StoryGenerationState.IMAGE_GENERATION) fromState;
        List<StoryPageDTO> storyPageDtos = currentState.storyPagesDto();
        int currentIndex = currentState.currentIndex();

        if (currentIndex >= storyPageDtos.size()) {
            return StoryGenerationState.COMPLETED;
        }

        try {
            int totalPages = storyPageDtos.size();
            int remainingProgress = 100 - currentState.currentProgress();
            int progressChunk = (totalPages > currentIndex) ? remainingProgress / (totalPages - currentIndex) : remainingProgress;
            int beforeChunk = (int) (progressChunk * 0.20);

            progressService.sendUpdate(context.getTaskId(), currentState.currentProgress() + beforeChunk, "Creating story page #" + (storyPageDtos.get(currentIndex).pageNumber()) + "...");
            Thread.sleep(shortDelay.toMillis());
            StoryPageDTO currentPageDto = storyPageDtos.get(currentIndex);

            // Generate image
            if (currentIndex % 2 != 0) {
                 StoryPageDTO previousPageDto = storyPageDtos.get(currentIndex - 1);
                 String imageContext = "First, " + previousPageDto.englishSummary() + ". Then, " + currentPageDto.englishSummary();

                 AIRequest<GeneratedImageDTO> imageRequest = AIRequest.builder()
                         .responseClass(GeneratedImageDTO.class)
                         .promptType(PromptType.STORY_IMAGE)
                         .language(context.getRequest().language())
                         .param("context", imageContext)
                         .build();

                 GeneratedImageDTO imageDto = aiEngine.generate(imageRequest).block();
                assert imageDto != null;
                String imageUrl = imageDto.url();
                currentPageDto = currentPageDto.withImageUrl(imageUrl);
            }

            StoryPage storyPage = storyPageService.createAndPersistPage(context.getShortStory(), currentPageDto, context.getPageCounter().getAndIncrement());

            progressService.sendPageUpdate(context.getTaskId(), currentState.currentProgress() + progressChunk, "Created page #" + storyPage.getPageNumber(), dtoMapper.toDto(storyPage));
            Thread.sleep(longDelay.toMillis());

            return StoryGenerationState.IMAGE_GENERATION(storyPageDtos, currentIndex + 1, currentState.currentProgress() + progressChunk);
        }
        catch (Exception e) {
            log.error("Page generation failed at index {}: {}", currentIndex, e.getMessage());
            return StoryGenerationState.FAILED("Page generation failed.");
        }
    }

}
