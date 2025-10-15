package com.example.language_learning.ai.contexts;

import com.example.language_learning.ai.config.model.AIPrompt;
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
    AtomicInteger attemptCounter,
    boolean withModeration,
    String language
) {}
