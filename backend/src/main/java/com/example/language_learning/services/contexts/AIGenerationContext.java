package com.example.language_learning.services.contexts;

import com.example.language_learning.config.AIConfig.AIPrompt;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public record AIGenerationContext (
    ChatClient chatClient,
    Map<String, Object> params,
    AIPrompt aiPrompt,
    JavaType apiDtoType,
    int maxRetries,
    AtomicInteger attemptCounter
) {}
