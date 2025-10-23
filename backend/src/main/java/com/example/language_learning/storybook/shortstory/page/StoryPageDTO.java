package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.shared.dtos.progress.ProgressData;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraphDTO;
import com.example.language_learning.storybook.shortstory.page.vocab.StoryVocabularyItemDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.With;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@With
public record StoryPageDTO(
    Long id,
    StoryPageType type,
    String englishSummary,
    String imageUrl,
    List<StoryParagraphDTO> paragraphs,
    List<StoryVocabularyItemDTO> vocabulary
) implements ProgressData {}
