package com.example.language_learning.ai.config.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import org.springframework.core.io.Resource;

@Builder
public record AIPrompt(Resource instruction, JsonNode schema) {
}