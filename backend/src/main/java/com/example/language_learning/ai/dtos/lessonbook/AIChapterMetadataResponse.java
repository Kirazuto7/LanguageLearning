package com.example.language_learning.ai.dtos.lessonbook;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the exact JSON structure for lessonChapter metadata from the AI.
 */
public record AIChapterMetadataResponse(
    @JsonProperty("title") String title,
    @JsonProperty("nativeTitle") String nativeTitle
) { }