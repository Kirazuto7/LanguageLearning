package com.example.language_learning.ai.config;

import ai.djl.repository.zoo.Criteria;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up the text embedding model.
 * This defines the criteria for loading a pre-trained sentence transformer model using DJL.
 */
@Configuration
@ConditionalOnProperty(name = "djl.embedding.enabled", havingValue = "true", matchIfMissing = true)
public class EmbeddingConfig {

    @Getter
    @Value("${djl.embedding.model-url}")
    private String modelUrl;

    @Bean
    public Criteria<String, float[]> sentenceEmbeddingCriteria() {
        return Criteria.builder()
                .setTypes(String.class, float[].class)
                .optModelUrls(modelUrl)
                .optEngine("PyTorch")
                .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                .optArgument("padding", "true")
                .optArgument("truncation", "true")
                .optArgument("pooling", "mean")
                .build();
    }
}
