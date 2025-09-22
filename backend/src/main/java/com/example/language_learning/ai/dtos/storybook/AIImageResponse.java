package com.example.language_learning.ai.dtos.storybook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Represents the top-level response from an AI image generation API call.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AIImageResponse(
    long created,
    List<AIImageData> data
) {}
