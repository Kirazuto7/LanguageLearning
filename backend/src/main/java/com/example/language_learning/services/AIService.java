package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.GrammarLessonDTO;
import com.example.language_learning.dto.lessons.ReadingComprehensionLessonDTO;
import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.requests.ChapterGenerationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.PromptTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
    @Value("classpath:prompts/chapter_metadata_prompt.txt")
    private Resource chapterMetadataPrompt;
    @Value("classpath:prompts/vocabulary_lesson_prompt.txt")
    private Resource vocabularyLessonPrompt;
    @Value("classpath:prompts/grammar_lesson_prompt.txt")
    private Resource grammarLessonPrompt;
    @Value("classpath:prompts/practice_lesson_prompt.txt")
    private Resource practiceLessonPrompt;
    @Value("classpath:prompts/reading_comprehension_lesson_prompt.txt")
    private Resource readingComprehensionLessonPrompt;

    public AIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Mono<ChapterMetadataDTO> generateChapterMetadata(ChapterGenerationRequest request) {
        return generateLessonComponent(request, chapterMetadataPrompt, ChapterMetadataDTO.class);
    }

    public Mono<VocabularyLessonDTO> generateVocabularyLesson(ChapterGenerationRequest request) {
        return generateLessonComponent(request, vocabularyLessonPrompt, VocabularyLessonDTO.class);
    }

    public Mono<GrammarLessonDTO> generateGrammarLesson(ChapterGenerationRequest request) {
        return generateLessonComponent(request, grammarLessonPrompt, GrammarLessonDTO.class);
    }

    public Mono<PracticeLessonDTO> generatePracticeLesson(ChapterGenerationRequest request) {
        return generateLessonComponent(request, practiceLessonPrompt, PracticeLessonDTO.class);
    }

    public Mono<ReadingComprehensionLessonDTO> generateReadingComprehensionLesson(ChapterGenerationRequest request) {
        return generateLessonComponent(request, readingComprehensionLessonPrompt, ReadingComprehensionLessonDTO.class);
    }

    private <T> Mono<T> generateLessonComponent(ChapterGenerationRequest request, Resource promptResource, Class<T> dtoClass) {
        String componentName = dtoClass.getSimpleName().replace("DTO", "");
        logger.info("Generating a {} for topic: {}", componentName,request.getTopic());
        var outputParser = new BeanOutputConverter<>(dtoClass);

        PromptTemplate promptTemplate;
        try {
            promptTemplate = new PromptTemplate(promptResource);
        } catch (Exception e) {
            logger.error("Failed to create prompt template from resource: {}. Check for syntax errors like unclosed '<' or '>'.", promptResource.getFilename(), e);
            return Mono.error(new IllegalArgumentException("Invalid prompt template: " + promptResource.getFilename(), e));
        }

        Map<String, Object> params = Map.of(
                "language", request.getLanguage(),
                "difficulty", request.getDifficulty(),
                "topic", request.getTopic()
        );

        var prompt = promptTemplate.create(params);
        logger.debug("Rendered Prompt for {}: {}", componentName, prompt.getContents());

        return chatClient.prompt()
                .user(prompt.getContents())
                .stream()
                .content()
                .collectList()
                .map(list -> String.join("", list))
                .doOnNext(rawResponse -> logger.info("Raw AI Response for {}: {}", componentName, rawResponse))
                .map(this::extractJson)
                .doOnNext(json -> logger.info("Extracted JSON for {}: {}", componentName, json))
                .map(outputParser::convert)
                .doOnNext(parsed -> logger.info("Parsed {}: {}", componentName, parsed))
                .doOnError(e -> logger.error("Failed to generate or parse AI response for {}.", componentName, e));
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
