package com.example.language_learning.services;

import com.example.language_learning.dto.ChapterResponse;
import com.example.language_learning.dto.GenerationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;


import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * AIService is a placeholder service class for handling AI-related operations.
 */
@Service
@RequiredArgsConstructor
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final ChatClient chatClient;

    public Mono<ChapterResponse> generateChapter(GenerationRequest request) {
        var outputParser = new BeanOutputParser<>(ChapterResponse.class);

        String prompt = """
                You are an expert language tutor, fluent in both English and {language}. Your most important duty is to provide 100% accurate translations.
                The student wants to learn {language}.
                The student's proficiency level is {level}.
                The topic for the lesson is: {topic}.

                Before generating the JSON, think step-by-step to ensure accuracy:
                1.  Identify 5-10 key English vocabulary words related to the topic "{topic}".
                2.  For each English word, find the most common and accurate translation in {language}.
                3.  For each {language} word, determine its standard romanization.
                4.  Assemble this information into the final JSON structure. Your internal thoughts should NOT be in the final output.

                Please generate a response in JSON format that includes the following fields:
                - "title": A creative and relevant title for the chapter in English.
                - "nativeTitle": The accurate translation of the title in {language}.
                - "lessons": An array of lesson objects. For now, please generate one lesson of type "vocabulary".

                Each lesson object in the "lessons" array should have:
                - "type": The type of lesson, which must be "vocabulary".
                - "title": A title for the vocabulary section, like "Key Vocabulary for {topic}".
                - "items": An array of 5-10 relevant vocabulary items.

                Each vocabulary item in the "items" array must have:
                - "word": The correct vocabulary word in {language}. This MUST be the accurate translation of the English "translation" field.
                - "romanization": The correct, standard romanization of the word. If not applicable, provide an empty string.
                - "translation": The accurate English translation of the word.
                
                ACCURACY IS PARAMOUNT. The "word" in {language} and its English "translation" must be a correct and common translation pair. Do not invent or guess words.

                Pay special attention to escaping special characters within the JSON string values. For example,
                any double quotes (") inside a string must be escaped with a backslash (\\").

                IMPORTANT: You must only output the raw JSON object. Do not include any conversational text,
                markdown formatting, code block fences, or any other text outside of the JSON structure itself.
                The JSON must be well-formed, valid, and complete. Ensure your entire response is a single JSON object and is not truncated.
                It is critical that the JSON response is not cut off and ends with a closing brace '}'.
                """;

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
                .map(outputParser::parse) // Parse the JSON string into a ChapterResponse object
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
