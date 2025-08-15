package com.example.language_learning.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the exact JSON structure for chapter metadata from the AI.
 */
public record AIChapterMetadataResponse(
    @JsonProperty("title") String title,
    @JsonProperty("nativeTitle") String nativeTitle
) { }