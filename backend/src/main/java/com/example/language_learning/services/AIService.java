package com.example.language_learning.services;

import com.example.language_learning.dto.ChapterResponse;
import com.example.language_learning.dto.GenerationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

/**
 * AIService is a placeholder service class for handling AI-related operations.
 */
@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private final ChatClient chatClient;

    public AIService(ChatClient.Builder chatClientBuilder, @Value("${app.ai.system-prompt}") String systemPrompt) {
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    public Mono<ChapterResponse> generateChapter(GenerationRequest request) {
        var outputParser = new BeanOutputConverter<>(ChapterResponse.class);
        String prompt = "Generate a language learning chapter about {topic} for a {level} level student of {language}.";

        return chatClient.prompt()
                .user(p -> p.text(prompt)
                        .param("language", request.getLanguage())
                        .param("level", request.getLevel())
                        .param("topic", request.getTopic()))
                .stream()
                .content()
                .collectList() // Collect all response chunks into a list
                .map(list -> String.join("", list)) // Join them into a single string
                .doOnNext(rawResponse -> logger.info("Raw AI Response: {}", rawResponse)) // Log the raw response for debugging
                .map(this::extractJson) // Extract only the JSON part of the response
                .map(outputParser::convert) // Parse the JSON string into a ChapterResponse object
                .doOnError(e -> logger.error("Failed to parse AI response.", e)); // Log any parsing errors
    }

    /**
     * Extracts a JSON object from a raw string response that may contain conversational text.
     *
     * @param rawResponse The raw string response from the AI.
     * @return A string containing only the JSON object.
     */
    private String extractJson(String rawResponse) {
        int firstBrace = rawResponse.indexOf('{');
        int lastBrace = rawResponse.lastIndexOf('}');

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return rawResponse.substring(firstBrace, lastBrace + 1);
        }
        return rawResponse; 
    }
}
