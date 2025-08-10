package com.example.language_learning.services;

import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.requests.ChapterGenerationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.PromptTemplate;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * AIService is a placeholder service class for handling AI-related operations.
 */
@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private final ChatClient chatClient;

    public AIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Mono<ChapterDTO> generateChapter(ChapterGenerationRequest request) {
        String promptStarter = """
                Please generate a response in JSON format that includes the following fields:
                - "title": A creative and relevant title for the chapter in English.
                - "nativeTitle": The accurate translation of the title in {language}.
                - "pages": An array of page objects. For now, please generate one page.
                
                Each page object in the "pages" array should have:
                - "pageNumber": The page number, starting from 1.
                - "lesson": A lesson object.""";
        var outputParser = new BeanOutputConverter<>(ChapterDTO.class);
        String initialPrompt = "Generate a language learning chapter about {topic} for a {difficulty} level student of {language}.";
        PromptTemplate promptTemplate = new PromptTemplate(initialPrompt);
        Map<String, Object> params = Map.of(
                "language", request.getLanguage(),
                "difficulty", request.getDifficulty(),
                "topic", request.getTopic()
        );
        String renderedPrompt = promptTemplate.render(params);
        System.out.println("Render Prompt: " + renderedPrompt);

        return chatClient.prompt()
                .user(renderedPrompt)
                .stream()
                .content()
                .collectList() // Collect all response chunks into a list
                .map(list -> String.join("", list)) // Join them into a single string
                .doOnNext(rawResponse -> logger.info("Raw AI Response: {}", rawResponse)) // Log the raw response for debugging
                .map(this::extractJson) // Extract only the JSON part of the response
                .doOnNext(json -> logger.info("Extracted JSON: {}", json)) // extracted JSON to feed into parser
                .map(outputParser::convert) // Parse the JSON string into a ChapterResponse object
                .doOnNext(parsed -> logger.info("Parsed ChapterResponse: {}", parsed)) // the parsed object
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
