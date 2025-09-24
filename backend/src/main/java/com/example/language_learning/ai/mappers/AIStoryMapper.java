package com.example.language_learning.ai.mappers;

import com.example.language_learning.ai.dtos.storybook.*;
import com.example.language_learning.shared.dtos.images.GeneratedImageDTO;
import com.example.language_learning.shared.services.ImageService;
import com.example.language_learning.storybook.shortstory.ShortStoryDTO;
import com.example.language_learning.storybook.shortstory.ShortStoryMetadataDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraphDTO;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class AIStoryMapper {

    private final ImageService imageService;

    public ShortStoryMetadataDTO toShortStoryMetadataDTO(AIStoryMetadataResponse response, String genre) {
        return new ShortStoryMetadataDTO(response.title(), response.nativeTitle(), response.topic(), genre);
    }

    public ShortStoryDTO toShortStoryDTO(AIGeneratedStoryResponse response, Map<String, Object> params) {
        String title = (String) params.get("title");
        String nativeTitle = (String) params.get("nativeTitle");
        String topic = (String) params.get("topic");
        String genre = (String) params.get("genre");
        int chapterNumber = (int) params.get("chapterNumber");

        // Convert lessonPages response into respective list of storypage dtos
        List<StoryPageDTO> storyPages = toStoryPageDTOs(response.pages());

        return ShortStoryDTO.builder()
                .title(title)
                .nativeTitle(nativeTitle)
                .topic(topic)
                .genre(genre)
                .chapterNumber(chapterNumber)
                .storyPages(storyPages)
                .build();
    }

    public GeneratedImageDTO toStoryImageDTO(AIImageResponse response, String originalPrompt) {
        if (response == null || response.images() == null || response.images().isEmpty()) {
            throw new IllegalStateException("AI response for image generation is empty or invalid.");
        }
        List<String> permanentUrls = response.images().stream()
                .map(imageService::saveImageFromBase64)
                .toList();
        return new GeneratedImageDTO(permanentUrls, originalPrompt);
    }

    private List<StoryPageDTO> toStoryPageDTOs(List<AIGeneratedPage> aiGeneratedPages) {
        List<StoryPageDTO> storyPages = new ArrayList<>();

        for (AIGeneratedPage aiGeneratedPage : aiGeneratedPages) {
            StoryPageDTO storyPage = StoryPageDTO.builder()
                    .englishSummary(aiGeneratedPage.englishSummary())
                    .paragraphs(toStoryParagraphDTOS(aiGeneratedPage.content()))
                    .vocabulary(aiGeneratedPage.vocabulary().stream().map(this::toStoryVocabularyItemDTO).toList())
                    .build();
            storyPages.add(storyPage);
        }

        return storyPages;
    }

    private StoryVocabularyItemDTO toStoryVocabularyItemDTO(AIVocabularyItem aiVocabularyItem) {
        return StoryVocabularyItemDTO.builder()
                .word(aiVocabularyItem.word())
                .translation(aiVocabularyItem.translation())
                .build();
    }

    private List<StoryParagraphDTO> toStoryParagraphDTOS(String pageContent) {
        if (pageContent == null || pageContent.isBlank()) {
            return Collections.emptyList();
        }

        String[] paragraphs = pageContent.split("\\n");

        return IntStream.range(0, paragraphs.length)
                .filter(i -> !paragraphs[i].isBlank())
                .mapToObj(i -> StoryParagraphDTO.builder()
                        .paragraphNumber(i + 1)
                        .content(paragraphs[i])
                        .build())
                .collect(Collectors.toList());
    }
}
