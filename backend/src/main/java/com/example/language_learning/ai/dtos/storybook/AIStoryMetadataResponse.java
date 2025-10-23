package com.example.language_learning.ai.dtos.storybook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIStoryMetadataResponse(
        String title,
        @JsonProperty("nativeTitle") String nativeTitle,
        String topic
) {}
