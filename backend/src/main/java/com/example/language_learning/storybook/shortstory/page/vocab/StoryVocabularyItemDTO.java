package com.example.language_learning.storybook.shortstory.page.vocab;

import lombok.Builder;

@Builder
public record StoryVocabularyItemDTO(
    Long id,
    String word,
    String translation
) {}
