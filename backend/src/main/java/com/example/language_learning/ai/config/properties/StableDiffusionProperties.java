package com.example.language_learning.ai.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.stablediffusion")
public record StableDiffusionProperties (
    String baseUrl,
    Image image
)   {
    public record Image(Options options) {}
    public record Options(String model, int width, int height, int steps, int batchSize) {}
}
