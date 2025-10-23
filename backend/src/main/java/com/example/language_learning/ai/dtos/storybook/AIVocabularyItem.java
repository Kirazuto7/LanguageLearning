package com.example.language_learning.ai.dtos.storybook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIVocabularyItem(
    String word,
    String translation
) {}
