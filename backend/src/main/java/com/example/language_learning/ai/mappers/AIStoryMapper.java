package com.example.language_learning.ai.mappers;

import com.example.language_learning.ai.dtos.storybook.*;
import com.example.language_learning.storybook.shortstory.ShortStoryDTO;
import com.example.language_learning.storybook.shortstory.ShortStoryMetadataDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageType;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraphDTO;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItemDTO;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class AIStoryMapper {

    public ShortStoryMetadataDTO toShortStoryMetadataDTO(AIStoryMetadataResponse response, String genre) {
        return new ShortStoryMetadataDTO(response.title(), response.nativeTitle(), response.topic(), genre);
    }

    public ShortStoryDTO toShortStoryDTO(AIGeneratedStoryResponse response, Map<String, Object> params) {
        String title = (String) params.get("title");
        String nativeTitle = (String) params.get("nativeTitle");
        String topic = (String) params.get("topic");
        String genre = (String) params.get("genre");

        // Convert lessonPages response into respective list of storypage dtos
        List<StoryPageDTO> storyPages = toStoryPageDTOs(response.pages());

        return ShortStoryDTO.builder()
                .title(title)
                .nativeTitle(nativeTitle)
                .topic(topic)
                .genre(genre)
                .storyPages(storyPages)
                .build();
    }

    private List<StoryPageDTO> toStoryPageDTOs(List<AIGeneratedPage> aiGeneratedPages) {
        List<StoryPageDTO> storyPages = new ArrayList<>();

        // Entire list of the vocabulary found in the short story
        List<StoryVocabularyItemDTO> storyVocabulary = new ArrayList<>();

        // Use a Set to track and avoid duplicate vocabulary items
        Set<String> encounteredWords = new HashSet<>();

        // 1. First make the content pages based on the number of aiGeneratedPages
        for (AIGeneratedPage aiGeneratedPage : aiGeneratedPages) {
            List<StoryVocabularyItemDTO> pageVocabulary = aiGeneratedPage.vocabulary().stream()
                    .map(this::toStoryVocabularyItemDTO)
                    .filter(Objects::nonNull)
                    .filter(item -> encounteredWords.add(item.word()))
                    .toList();
            storyVocabulary.addAll(pageVocabulary);

            StoryPageDTO storyPage = StoryPageDTO.builder()
                    .type(StoryPageType.CONTENT)
                    .englishSummary(aiGeneratedPage.englishSummary())
                    .paragraphs(toStoryParagraphDTOS(aiGeneratedPage.content()))
                    .vocabulary(pageVocabulary)
                    .build();
            storyPages.add(storyPage);
        }

        // 2. Create the final page consisting of all the vocabulary items
        StoryPageDTO vocabPage = StoryPageDTO.builder()
                .type(StoryPageType.VOCABULARY)
                .vocabulary(storyVocabulary)
                .build();
        storyPages.add(vocabPage);
        return storyPages;
    }

    private StoryVocabularyItemDTO toStoryVocabularyItemDTO(AIVocabularyItem aiVocabularyItem) {
        if (aiVocabularyItem.word() == null || aiVocabularyItem.word().isBlank()) {
            return  null;
        }
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
