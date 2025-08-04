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
                You are an expert language tutor. Your task is to create a high-quality language lesson for a student.
                The student wants to learn {language}.
                The student's proficiency level is {level}.
                The topic for the lesson is: {topic}.

                Please generate a response in JSON format that includes the following fields:
                - "title": A creative and relevant title for the chapter in English.
                - "nativeTitle": The accurate translation of the title in {language}.
                - "lessons": An array of lesson objects. For now, please generate one lesson of type "vocabulary".

                Each lesson object in the "lessons" array should have:
                - "type": The type of lesson. For now, use "vocabulary".
                - "title": A title for the vocabulary section, like "Key Vocabulary for {topic}".
                - "items": An array of 5-10 relevant vocabulary items.

                Each vocabulary item in the "items" array must have:
                - "word": The correct vocabulary word in {language}.
                - "romanization": The correct, standard romanization of the word. If not applicable, provide an empty string.
                - "translation": The accurate English translation of the word.
                
                Pay special attention to escaping special characters within the JSON string values. For example,
                any double quotes (") inside a string must be escaped with a backslash (\\").

                IMPORTANT: You must only output the raw JSON object. Do not include any conversational text,
                markdown formatting, code block fences, or any other text outside of the JSON structure itself.
                The JSON must be well-formed, valid, and complete. Do not truncate the response.
                
                {format}
                """;

        return chatClient.prompt()
                .user(p -> p.text(prompt)
                        .param("language", request.getLanguage())
                        .param("level", request.getLevel())
                        .param("topic", request.getTopic())
                        .param("format", outputParser.getFormat()))
                .stream()
                .content()
                .collectList() // Collect all response chunks into a list
                .map(list -> String.join("", list)) // Join them into a single string
                .doOnNext(rawJson -> logger.info("Raw AI Response: {}", rawJson)) // Log the raw response for debugging
                .map(outputParser::parse) // Parse the JSON string into a ChapterResponse object
                .doOnError(e -> logger.error("Failed to parse AI response.", e)); // Log any parsing errors
    }
}
