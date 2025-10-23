package com.example.language_learning.ai.dtos.storybook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIGeneratedStoryResponse(
    List<AIGeneratedPage> pages
) {}
