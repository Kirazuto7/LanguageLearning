package com.example.language_learning.services;

import com.example.language_learning.dto.api.*;
import com.example.language_learning.dto.languages.WordDTO;
import com.example.language_learning.dto.lessons.GrammarLessonDTO;
import com.example.language_learning.dto.lessons.ReadingComprehensionLessonDTO;
import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.mapper.ApiDtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.PromptTemplate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A service dedicated to interacting with the AI model.
 * <p>
 * This service acts as a bridge between the application's standard (blocking) MVC architecture
 * and the AI client's reactive (non-blocking) nature. It returns reactive types (Mono)
 * so that calling services (like {@link ChapterService}) can create a sequential, multi-stage
 * generation pipeline. Each step can build on the context of the previous one, leading to
 * more coherent and contextually relevant content.
 */
@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private static final String DEFAULT_MODEL_NAME = "qwen3";
    private static final Map<String, String> LANGUAGE_MODEL_MAP;
    static {
        LANGUAGE_MODEL_MAP = Map.of(
          "korean", "exaone",
          "japanese", "qwen3",
          "default", DEFAULT_MODEL_NAME
        );
    }
    private final Map<String, ChatClient> chatClients;
    private final ApiDtoMapper apiDtoMapper;
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

    public AIService(Map<String, ChatClient> chatClients, ApiDtoMapper apiDtoMapper) {
        this.chatClients = chatClients;
        this.apiDtoMapper = apiDtoMapper;
        logger.info("--- Verifying Injected ChatClients ---");
        logger.info("Found {} ChatClient bean(s):", chatClients.size());
        chatClients.keySet().forEach(key -> logger.info(" -> Bean name: '{}'", key));
        logger.info("--------------------------------------");
    }

    public Mono<ChapterMetadataDTO> generateChapterMetadata(ChapterGenerationRequest request) {
        Map<String, Object> params = Map.of(
                "language", request.getLanguage(),
                "difficulty", request.getDifficulty(),
                "topic", request.getTopic()
        );

        return generateLessonComponent(
                params,
                chapterMetadataPrompt,
                AIChapterMetadataResponse.class,
                apiResponse -> apiDtoMapper.toChapterMetadataDTO(apiResponse, request.getTopic()));
    }

    public Mono<VocabularyLessonDTO> generateVocabularyLesson(ChapterGenerationRequest request, ChapterMetadataDTO metadata) {
        Map<String, Object> params = new HashMap<>();
        params.put("language", request.getLanguage());
        params.put("difficulty", request.getDifficulty());
        params.put("topic", request.getTopic());
        params.put("chapterTitle", metadata.getTitle());
        params.put("nativeChapterTitle", metadata.getNativeTitle());

        return generateLessonComponent(params, vocabularyLessonPrompt, AIVocabularyLessonResponse.class, apiDtoMapper::toVocabularyLessonDTO);
    }

    public Mono<GrammarLessonDTO> generateGrammarLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary) {
        Map<String, Object> params = new HashMap<>();
        params.put("language", request.getLanguage());
        params.put("difficulty", request.getDifficulty());
        params.put("topic", request.getTopic());
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.getVocabularies()));

        return generateLessonComponent(params, grammarLessonPrompt, AIGrammarLessonResponse.class, apiDtoMapper::toGrammarLessonDTO);
    }

    public Mono<PracticeLessonDTO> generatePracticeLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary, GrammarLessonDTO grammar) {
        Map<String, Object> params = new HashMap<>();
        params.put("language", request.getLanguage());
        params.put("difficulty", request.getDifficulty());
        params.put("topic", request.getTopic());
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.getVocabularies()));
        params.put("grammarConcept", grammar.getGrammarConcept());

        return generateLessonComponent(params, practiceLessonPrompt, AIPracticeLessonResponse.class, apiDtoMapper::toPracticeLessonDTO);
    }

    public Mono<ReadingComprehensionLessonDTO> generateReadingComprehensionLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary, GrammarLessonDTO grammar) {
        Map<String, Object> params = new HashMap<>();
        params.put("language", request.getLanguage());
        params.put("difficulty", request.getDifficulty());
        params.put("topic", request.getTopic());
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.getVocabularies()));
        params.put("grammarConcept", grammar.getGrammarConcept());

        return generateLessonComponent(params, readingComprehensionLessonPrompt, AIReadingComprehensionLessonResponse.class, apiDtoMapper::toReadingComprehensionLessonDTO);
    }

    /**
     * A generic method to generate any lesson component.
     * It first parses the AI response into a dedicated API DTO, then maps it to the application's internal DTO.
     *
     * @param params         A map of parameters to be injected into the prompt template.
     * @param promptResource The prompt template to use.
     * @param apiDtoClass    The class of the API-specific DTO to parse into (e.g., AIGrammarLessonResponse.class).
     * @param mapperFunction The function to map from the API DTO to the internal DTO (e.g., apiDtoMapper::toGrammarLessonDTO).
     * @param <T_API>        The type of the API DTO.
     * @param <T_INTERNAL> The type of the internal application DTO.
     * @return A Mono containing the final, internal DTO.
     */
    private <T_API, T_INTERNAL> Mono<T_INTERNAL> generateLessonComponent(
            Map<String, Object> params,
            Resource promptResource,
            Class<T_API> apiDtoClass,
            Function<T_API, T_INTERNAL> mapperFunction) {
        String componentName = apiDtoClass.getSimpleName().replace("Response", "").replace("AI", "");
        logger.info("Generating a {} for topic: {}", componentName, params.get("topic"));
        var outputParser = new BeanOutputConverter<>(apiDtoClass);
        PromptTemplate promptTemplate = createPromptTemplate(promptResource);

        var prompt = promptTemplate.create(params);
        logger.debug("Rendered Prompt for {}: {}", componentName, prompt.getContents());
        String language = (String) params.get("language");
        ChatClient chatClient = selectClient(language);

        return chatClient.prompt()
                .user(prompt.getContents())
                .stream().content().collectList()
                .map(list -> String.join("", list))
                .doOnNext(rawResponse -> logger.info("Raw AI Response for {}: {}", componentName, rawResponse))
                .map(this::extractJson)
                .doOnNext(json -> logger.debug("Extracted JSON for {}: {}", componentName, json))
                .map(outputParser::convert)
                .map(mapperFunction)
                .doOnNext(mapped -> logger.info("Mapped to internal DTO {}: {}", componentName, mapped))
                .doOnError(e -> logger.error("Failed to generate or parse AI response for {}.", componentName, e));
    }

    /**
     * Formats a list of vocabulary words into a simple, comma-separated string suitable for AI prompts.
     * This method is polymorphic and handles different language-specific word types.
     *
     * @param vocabularies The list of vocabulary words.
     * @return A formatted string of the words.
     */
    private String formatVocabularyForPrompt(List<WordDTO> vocabularies) {
        if (vocabularies == null || vocabularies.isEmpty()) {
            return "No specific vocabulary provided.";
        }
        return vocabularies.stream()
                .map(WordDTO::getPrimaryRepresentation)
                .collect(Collectors.joining(", "));
    }

    private PromptTemplate createPromptTemplate(Resource resource) {
        try {
            return new PromptTemplate(resource);
        } catch (Exception e) {
            logger.error("Failed to create prompt template from resource: {}. Check for syntax errors like unclosed '<' or '>'.", resource.getFilename(), e);
            throw new IllegalArgumentException("Invalid prompt template: " + resource.getFilename(), e);
        }
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

    /**
     * Selects a ChatClient based on the chosen language setting.
     *
     * @param language The language setting used to select the ChatClient to use.
     * @return The Chat client for the selected language.
     */
    private ChatClient selectClient(String language) {
        if(language == null || language.isBlank()) {
            throw new IllegalArgumentException("Language cannot be null or empty.");
        }
        String modelName = LANGUAGE_MODEL_MAP.getOrDefault(language.toLowerCase(), LANGUAGE_MODEL_MAP.get("default"));
        ChatClient client = chatClients.get(modelName);

        if (client == null) {
            logger.error("Could not find a ChatClient bean named '{}'. Available beans are: {}", modelName, chatClients.keySet());
            throw new IllegalStateException("AI model client not configured: " + modelName);
        }
        return client;
    }
}
