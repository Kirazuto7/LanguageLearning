package com.example.language_learning.ai.dtos.storybook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIImageData(
        String url,
        @JsonProperty("revised_prompt") String revisedPrompt
) {}
