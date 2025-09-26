package com.example.language_learning.ai.config;

import com.example.language_learning.ai.config.properties.StableDiffusionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties({StableDiffusionProperties.class})
public class AIImageClientConfig {
    @Bean("imageApiWebClient")
    public WebClient imageApiWebClient(StableDiffusionProperties properties) {
        final int size = 16 * 1024 * 1024; // 16MB
        return WebClient.builder()
                .baseUrl(properties.baseUrl())
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
    }
}
