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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        log.info("Entering handleInitialGeneration for story task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 10 , "Initializing story generation...");
            Thread.sleep(shortDelay.toMillis());

            ShortStory shortStory = shortStoryService.getShortStory(context.getStoryId())
                    .orElseThrow(() -> new RuntimeException("ShortStory not found for async generation: " + context.getStoryId()));
            log.info("Successfully fetched story. ID: {}", shortStory.getId());

            context.setShortStory(shortStory);

            return StoryGenerationState.METADATA;
        }
        catch (Exception e) {
            log.error("Error during story generation initialization for task {}: {}", context.getTaskId(), e.getMessage(), e);
            return StoryGenerationState.FAILED(e.getMessage());
        }
    }

    public StoryGenerationState handleMetadataGeneration(StoryGenerationState fromState, StoryGenerationContext context) {
        log.info("Entering handleMetadataGeneration for story task ID: {}", context.getTaskId());
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
            log.info("Generated story metadata: {}", metadata);

            ShortStory shortStory = context.getShortStory();
            assert metadata != null;
            shortStory.setTitle(metadata.title());
            shortStory.setNativeTitle(metadata.nativeTitle());
            shortStory.setTopic(metadata.topic());
            shortStory.setGenre(metadata.genre());
            shortStoryService.saveShortStory(shortStory);
            log.info("Updated story metadata and saved. Transitioning to STORY_GENERATION state for task ID: {}", context.getTaskId());
            return StoryGenerationState.STORY_GENERATION(metadata);
        }
        catch (Exception e) {
            log.error("Error in handleMetadataGeneration for story task ID: {}", context.getTaskId(), e);
            return StoryGenerationState.FAILED(e.getMessage());
        }
    }

    public StoryGenerationState handleStoryGeneration(StoryGenerationState fromState, StoryGenerationContext context) {
        log.info("Entering handleStoryGeneration for story task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 30, "Writing the story...");
            Thread.sleep(shortDelay.toMillis());

            ShortStoryMetadataDTO metadata = ((StoryGenerationState.STORY_GENERATION) fromState).metadataDto();
            int chapterNumber = context.getShortStory().getChapterNumber();
            AIRequest<ShortStoryDTO> aiRequest = AIRequest.builder()
                    .responseClass(ShortStoryDTO.class)
                    .promptType(PromptType.STORY_PAGES)
                    .language(context.getRequest().language())
                    .param("topic", metadata.topic())
                    .param("genre", context.getRequest().genre())
                    .param("difficulty", context.getRequest().difficulty())
                    .param("storyTitle", metadata.title())
                    .param("nativeStoryTitle", metadata.nativeTitle())
                    .param("chapterNumber", chapterNumber)
                    .build();

            ShortStoryDTO storyDto = aiEngine.generate(aiRequest).block();
            assert storyDto != null;
            log.info("Generated story with {} pages. Transitioning to IMAGE_GENERATION state for task ID: {}", storyDto.storyPages().size(), context.getTaskId());
            return StoryGenerationState.IMAGE_GENERATION(storyDto.storyPages());
        }
        catch (Exception e) {
            log.error("Error in handleStoryGeneration for story task ID: {}", context.getTaskId(), e);
            return StoryGenerationState.FAILED(e.getMessage());
        }
    }

    public StoryGenerationState handleImageGeneration(StoryGenerationState fromState, StoryGenerationContext context) {
        log.info("Entering handleImageGeneration for story task ID: {}", context.getTaskId());
        StoryGenerationState.IMAGE_GENERATION currentState = (StoryGenerationState.IMAGE_GENERATION) fromState;
        List<StoryPageDTO> storyPageDtos = currentState.storyPagesDto();

        try {
            progressService.sendUpdate(context.getTaskId(), 40, "Creating illustrations for the story...");
            Thread.sleep(shortDelay.toMillis());
            String imageContext = storyPageDtos.stream()
                    .map(StoryPageDTO::englishSummary)
                    .collect(Collectors.joining("\n"));
            log.info("Image generation context: {}", imageContext);

            AIRequest<GeneratedImageDTO> imageRequest = AIRequest.builder()
                    .responseClass(GeneratedImageDTO.class)
                    .promptType(PromptType.STORY_IMAGE)
                    .language(context.getRequest().language())
                    .param("context", imageContext)
                    .build();

            GeneratedImageDTO imageDTO = aiEngine.generateImages(imageRequest).block();
            assert imageDTO != null;
            log.info("Generated {} images. Transitioning to PERSIST_PAGES state for task ID: {}", imageDTO.urls().size(), context.getTaskId());
            List<String> permanentUrls = imageDTO.urls();

            List<StoryPageDTO> updatedDtos = new ArrayList<>();
            for (int i = 0; i < storyPageDtos.size(); i++) {
                StoryPageDTO originalDto = storyPageDtos.get(i);
                String imageUrl = (i < permanentUrls.size()) ? permanentUrls.get(i) : null;
                updatedDtos.add(originalDto.withImageUrl(imageUrl));
            }

            return StoryGenerationState.PERSIST_PAGES(updatedDtos);
        }
        catch (Exception e) {
            log.error("Error in handleImageGeneration for story task ID: {}", context.getTaskId(), e);
            return StoryGenerationState.FAILED(e.getMessage());
        }

    }

    public StoryGenerationState handlePersistPages(StoryGenerationState fromState, StoryGenerationContext context) {
        log.info("Entering handlePersistPages for story task ID: {}", context.getTaskId());
        StoryGenerationState.PERSIST_PAGES currentState = (StoryGenerationState.PERSIST_PAGES) fromState;
        List<StoryPageDTO> storyPageDtos = currentState.storyPagesDto();
        int currentIndex = currentState.currentIndex();

        if (currentIndex >= storyPageDtos.size()) {
            return StoryGenerationState.COMPLETED;
        }

        try {
            int totalPages = storyPageDtos.size();
            int startProgress = currentState.currentProgress();
            int remainingProgress = 100 - startProgress;
            int progressChunk = (totalPages > currentIndex) ? remainingProgress / (totalPages - currentIndex) : remainingProgress;

            StoryPageDTO currentPageDto = storyPageDtos.get(currentIndex);

            progressService.sendUpdate(context.getTaskId(), startProgress, "Saving page " + (currentIndex + 1) + " of " + totalPages + "...");
            Thread.sleep(shortDelay.toMillis());

            StoryPage storyPage = storyPageService.createAndPersistPage(context.getShortStory(), currentPageDto, context.getPageCounter().getAndIncrement());

            StoryPageDTO pageDto = dtoMapper.toDto(storyPage);
            log.info("Sending story page update for task ID: {}. DTO: {}", context.getTaskId(), pageDto);
            progressService.sendPageUpdate(context.getTaskId(), startProgress + progressChunk, "Saved page #" + storyPage.getPageNumber(), pageDto);
            Thread.sleep(longDelay.toMillis());

            return StoryGenerationState.PERSIST_PAGES(storyPageDtos, currentIndex + 1, startProgress + progressChunk);
        }
        catch (Exception e) {
            log.error("Failed to persist story page at index {}: {}", currentIndex, e.getMessage(), e);
            return StoryGenerationState.FAILED("Page generation failed.");
        }
    }

}
