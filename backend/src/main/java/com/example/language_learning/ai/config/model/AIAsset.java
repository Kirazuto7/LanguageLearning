package com.example.language_learning.ai.config.model;

import com.example.language_learning.ai.enums.PromptType;
import lombok.Builder;

import java.util.Map;

@Builder
public record AIAsset(String modelName, Map<PromptType, AIPrompt> prompts) {
}