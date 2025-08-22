package com.example.language_learning.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AIJapaneseVocabularyLessonResponse (
        @JsonProperty("title") String title,
        @JsonProperty("vocabularies") List<AIJapaneseVocabularyItemDTO> vocabularies
)
{}
