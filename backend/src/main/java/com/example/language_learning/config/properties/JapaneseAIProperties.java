package com.example.language_learning.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.ollama3")
public record JapaneseAIProperties(String baseUrl, ChatProperties chat) {
    public record ChatProperties(String model, Options options) {}
    public record Options(String format, int numCtx, int numPredict, double temperature) {}
}
