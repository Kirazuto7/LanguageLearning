package com.example.language_learning.ai.config;

import com.example.language_learning.ai.config.model.StableDiffusionImageResponse;
import com.example.language_learning.ai.config.properties.StableDiffusionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StableDiffusionClient {
    private final WebClient webClient;
    private final StableDiffusionProperties properties;

    public StableDiffusionClient(@Qualifier("imageApiWebClient") WebClient webClient, StableDiffusionProperties properties) {
        this.properties = properties;
        this.webClient = webClient;
    }

    public StableDiffusionImageResponse promptTextToImage(String prompt) {
        log.info("Generating image for prompt: \"{}\"...", prompt.substring(0, Math.min(prompt.length(), 100)));

        Map<String, Object> request = Map.of(
            "prompt", prompt,
            "steps", properties.image().options().steps(),
            "width", properties.image().options().width(),
            "height", properties.image().options().height(),
            "batch_size", properties.image().options().batchSize()
        );
        try {
            StableDiffusionImageResponse response = webClient.post()
                    .uri("/sdapi/v1/txt2img")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(StableDiffusionImageResponse.class)
                    .block();

            if (response == null || response.getImages() == null || response.getImages().isEmpty()) {
                throw new IllegalStateException("API response did not contain any images.");
            }

            return response;
        }
        catch (Exception e) {
            log.error("Failed to generate image for prompt: \"{}\"", prompt, e);
            throw new RuntimeException("Failed to generate image for prompt: " + prompt, e);
        }
    }
}
