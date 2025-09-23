package com.example.language_learning.ai.dtos.storybook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Represents the raw JSON response from the AUTOMATIC1111 Stable Diffusion API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AIImageResponse(
        List<String> images,
        Map<String, Object> parameters,
        String info
) {}
