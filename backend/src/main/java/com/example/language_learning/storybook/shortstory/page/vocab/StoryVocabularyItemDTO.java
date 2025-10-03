package com.example.language_learning.storybook.shortstory.page.vocab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.With;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@With
public record StoryVocabularyItemDTO(
    Long id,
    String word,
    String translation,
    String stem
) {}
