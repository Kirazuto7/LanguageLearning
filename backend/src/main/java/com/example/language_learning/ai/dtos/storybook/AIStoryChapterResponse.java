package com.example.language_learning.ai.dtos.storybook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the full JSON structure for a generated story chapter from the AI.
 */
public record AIStoryChapterResponse(
        @JsonProperty("title") String title,
        @JsonProperty("nativeTitle") String nativeTitle,
        @JsonProperty("pages") List<AIStoryPage> pages
) {

    /**
     * Represents a single page within the story chapter.
     */
    public record AIStoryPage(
            @JsonProperty("pageNumber") int pageNumber,
            @JsonProperty("paragraphs") List<AIStoryParagraph> paragraphs,
            @JsonProperty("vocabulary") List<AIVocabularyWord> vocabulary
    ) {
    }

    /**
     * Represents a single paragraph of text on a page.
     */
    public record AIStoryParagraph(
            @JsonProperty("paragraphNumber") int paragraphNumber,
            @JsonProperty("content") String content
    ) {
    }

    /**
     * Represents a single vocabulary word with its translation and part of speech.
     */
    public record AIVocabularyWord(
            @JsonProperty("word") String word,
            @JsonProperty("translation") String translation,
            @JsonProperty("partOfSpeech") String partOfSpeech
    ) {
    }
}