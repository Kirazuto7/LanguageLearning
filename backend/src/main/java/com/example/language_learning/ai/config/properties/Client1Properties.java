package com.example.language_learning.ai.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.ollama1")
public record Client1Properties(String baseUrl, ChatProperties chat) {
    public record ChatProperties(String model, Options options) {}
    public record Options(String format, int numCtx, int numPredict, double temperature) {}
}
