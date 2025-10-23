package com.example.language_learning.ai.actions;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.ai.components.AIImageRequest;
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
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageService;
import com.example.language_learning.storybook.shortstory.page.StoryPageType;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class StoryGenerationActions {
    private final AIEngine aiEngine;
    private final ProgressService progressService;
    private final ShortStoryService shortStoryService;
    private final StoryPageService storyPageService;
    private final Duration shortDelay = Duration.ofSeconds(2);
    private final Duration longDelay = Duration.ofSeconds(4);

    public StoryGenerationState handleInitialGeneration(StoryGenerationState fromState, StoryGenerationContext context) {
        log.info("Entering handleInitialGeneration for story task ID: {}", context.getTaskId());
        try {
            progressService.sendUpdate(context.getTaskId(), 10 , "Initializing story generation...", context.getUser());
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
            progressService.sendUpdate(context.getTaskId(), 20, "Preparing story data...", context.getUser());

            AIRequest<ShortStoryMetadataDTO> aiRequest = AIRequest.builder()
                    .responseClass(ShortStoryMetadataDTO.class)
                    .language(context.getRequest().language())
                    .promptType(PromptType.STORY_METADATA)
                    .param("topic", context.getShortStory().getTopic())
                    .param("genre", context.getRequest().genre())
                    .param("difficulty", context.getRequest().difficulty())
                    .withModeration(true)
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
            progressService.sendUpdate(context.getTaskId(), 40, "Writing the story...", context.getUser());
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
            progressService.sendUpdate(context.getTaskId(), 70, "Creating illustrations for the story...", context.getUser());
            Thread.sleep(shortDelay.toMillis());

            Map<String, StoryPageDTO> pagesBySummary = storyPageDtos.stream()
                    .filter(dto -> dto.type() == StoryPageType.CONTENT && dto.englishSummary() != null && !dto.englishSummary().isBlank())
                    .collect(Collectors.toMap(
                        StoryPageDTO::englishSummary,
                        page -> page,
                            (existing, replacement) -> existing
                    ));
            List<String> imagePrompts = new ArrayList<>(pagesBySummary.keySet());

            String imageContext = String.join("\n", imagePrompts);
            log.info("Image generation context: {}", imageContext);

            AIImageRequest<GeneratedImageDTO> imageRequest = AIImageRequest.builder()
                    .responseClass(GeneratedImageDTO.class)
                    .param("context", imageContext)
                    .build();

            GeneratedImageDTO imageDTO = aiEngine.generateImages(imageRequest).block();
            assert imageDTO != null;
            log.info("Generated {} images. Transitioning to PERSIST_PAGES state for task ID: {}", imageDTO.imageUrlsByPrompt().size(), context.getTaskId());
            Map<String, String> permanentUrls = imageDTO.imageUrlsByPrompt();

            List<StoryPageDTO> updatedDtos = new ArrayList<>();
            for (int i = 0; i < storyPageDtos.size(); i++) {
                StoryPageDTO originalDto = storyPageDtos.get(i);

                if (originalDto.type() == StoryPageType.CONTENT && pagesBySummary.get(originalDto.englishSummary()) != null) {
                    String imageUrl = permanentUrls.get(originalDto.englishSummary());
                    updatedDtos.add(originalDto.withImageUrl(imageUrl));
                }
                else {
                    updatedDtos.add(originalDto);
                }
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

        try {
            progressService.sendUpdate(context.getTaskId(), 90, "Proofreading the story pages...", context.getUser());
            Thread.sleep(shortDelay.toMillis());

            List<StoryPageDTO> initialPageDtos = currentState.storyPagesDto();
            Map<String, StoryVocabularyItemDTO> masterVocabMap = new HashMap<>();
            List<StoryPageDTO> finalPageDtos = new ArrayList<>();

            for (StoryPageDTO dto : initialPageDtos) {
                StoryPageDTO processedDto = dto;

                if (dto.type() == StoryPageType.CONTENT) {
                    // Collect all vocabulary from content pages into a master map
                    // to build the final vocabulary page.
                    dto.vocabulary().forEach(v -> masterVocabMap.put(v.word(), v));
                }
                finalPageDtos.add(processedDto);
            }

            // Update the final vocabulary page with the master list
            int lastPageIndex = finalPageDtos.size() - 1;
            StoryPageDTO vocabPageDto = finalPageDtos.get(lastPageIndex);
            if (vocabPageDto.type() == StoryPageType.VOCABULARY) {
                finalPageDtos.set(lastPageIndex, vocabPageDto.withVocabulary(new ArrayList<>(masterVocabMap.values())));
            }

            storyPageService.batchCreateAndPersistPages(context.getShortStory(), finalPageDtos);
            log.info("Successfully persisted {} pages for task ID: {}", finalPageDtos.size(), context.getTaskId());

            progressService.sendUpdate(context.getTaskId(), 100, "Story complete!", context.getUser());
            Thread.sleep(longDelay.toMillis());

            return StoryGenerationState.COMPLETED;
        }
        catch (Exception e) {
            log.error("Failed to persist story pages for task {}: {}", context.getTaskId(), e.getMessage(), e);
            return StoryGenerationState.FAILED("Page generation failed.");
        }
    }

}
