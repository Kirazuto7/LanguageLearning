package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraphDTO;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItemDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record StoryPageDTO(
    Long id,
    String englishSummary,
    String imageUrl,
    int pageNumber,
    List<StoryParagraphDTO> paragraphs,
    List<StoryVocabularyItemDTO> vocabulary
) {}
