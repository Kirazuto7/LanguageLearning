package com.example.language_learning.ai.config;

import com.example.language_learning.ai.config.model.StableDiffusionImageResponse;
import com.example.language_learning.ai.config.properties.StableDiffusionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

        // Combine the main prompt with the configured styles into a single string.
        // This is the correct way to apply dynamic styles and LoRAs with the API.
        String fullPrompt = prompt + ", " + String.join(", ", properties.image().options().styles());

        Map<String, Object> requestBody = Map.of(
                "prompt", fullPrompt,
                "steps", properties.image().options().steps(),
                "width", properties.image().options().width(),
                "height", properties.image().options().height(),
                "batch_size", properties.image().options().batchSize(),
                "negative_prompt", properties.image().options().negativePrompt(),
                "cfg_scale", properties.image().options().cfgScale(),
                "sampler_name", properties.image().options().samplerName()
        );
        log.info("Image Request Body: {}", requestBody);
        
        try {
            return webClient.post()
                    .uri("/sdapi/v1/txt2img")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            log.info("Received successful response from image API.");
                            return response.bodyToMono(StableDiffusionImageResponse.class);
                        }
                        else {
                            log.error("Received error response from image API: {}", response.statusCode());
                            return response.bodyToMono(String.class)
                                    .doOnNext(body -> log.error("Error body: {}", body))
                                    .then(Mono.error(new RuntimeException("Image API returned status: " + response.statusCode())));
                        }
                    })
                    .block(Duration.ofMinutes(5)); // Add a generous 5-minute timeout for image generation
        }
        catch (Exception e) {
            log.error("Failed to generate image for prompt: \"{}\"", prompt, e);
            throw new RuntimeException("Failed to generate image for prompt: " + prompt, e);
        }
    }
}
