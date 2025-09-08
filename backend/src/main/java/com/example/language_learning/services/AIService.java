package com.example.language_learning.services;

import com.example.language_learning.config.AIConfig;
import com.example.language_learning.dto.api.*;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.WordDTO;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.enums.PromptType;
import com.example.language_learning.mapper.ApiDtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;

import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;

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
 * so that calling services (like {@link ChapterGenerationService}) can create a sequential, multi-stage
 * generation pipeline. Each step can build on the context of the previous one, leading to
 * more coherent and contextually relevant content.
 */
@Service
@Slf4j
public class AIService {

    private final Map<String, ChatClient> chatClients;
    private final ApiDtoMapper apiDtoMapper;
    private final ObjectMapper objectMapper;
    private final AIConfig aiConfig;

    public AIService(AIConfig aiConfig, ApiDtoMapper apiDtoMapper, ObjectMapper objectMapper, Map<String, ChatClient> chatClients) {
        this.aiConfig = aiConfig;
        this.apiDtoMapper = apiDtoMapper;
        this.objectMapper = objectMapper;
        this.chatClients = chatClients;
        log.info("--- Verifying Injected ChatClients ---");
        log.info("Found {} ChatClient bean(s):", chatClients.size());
        chatClients.keySet().forEach(key -> log.info(" -> Bean name: '{}'", key));
        log.info("--------------------------------------");
    }

    /** Practice Lesson Methods **/
    public Mono<PracticeLessonCheckResponse> proofRead(String originalQuestion, String userSentence, String language, String difficulty) {
        log.info("Proofreading question: {}", originalQuestion);

        Map<String, Object> params = new HashMap<>();
        params.put("language", language);
        params.put("question", originalQuestion);
        params.put("sentence", userSentence);
        params.put("difficulty", difficulty);

        Resource promptResource = getPromptOrThrow(language, PromptType.PROOFREAD);

        var outputParser = new BeanOutputConverter<>(AIProofreadResponse.class);
        PromptTemplate promptTemplate = createPromptTemplate(promptResource);
        var prompt = promptTemplate.create(params);
        log.debug("Rendered Prompt: {}", prompt.getContents());

        ChatClient chatClient = selectClient(language);

        return chatClient.prompt()
                .user(prompt.getContents())
                .stream().content().collectList()
                .map(list -> String.join("", list).trim())
                .doOnNext(rawResponse -> log.info("Raw AI Response: {}", rawResponse))
                .map(this::extractAndSanitizeJson)
                .doOnNext(json -> log.debug("Extracted JSON: {}", json))
                .map(outputParser::convert)
                .map(apiDtoMapper::toPracticeLessonCheckResponse)
                .doOnNext(mapped -> log.info("Mapped to internal DTO: {}", mapped))
                .doOnError(e -> log.error("Failed to generate or parse AI response.", e));
    }

    /** Chapter Generation Methods **/

    public Mono<ChapterMetadataDTO> generateChapterMetadata(ChapterGenerationRequest request) {
        Map<String, Object> params = createBaseParams(request);

        Resource prompt = getPromptOrThrow(request.language(), PromptType.METADATA);

        return generateLessonComponent(
                params,
                prompt,
                AIChapterMetadataResponse.class,
                apiResponse -> apiDtoMapper.toChapterMetadataDTO(apiResponse, request.topic()));
    }

    public Mono<VocabularyLessonDTO> generateVocabularyLesson(ChapterGenerationRequest request, ChapterMetadataDTO metadata) {
        Map<String, Object> params = createBaseParams(request);
        params.put("chapterTitle", metadata.title());
        params.put("nativeChapterTitle", metadata.nativeTitle());

        Resource prompt = getPromptOrThrow(request.language(), PromptType.VOCABULARY);

        if ("Japanese".equalsIgnoreCase(request.language())) {
            return generateLessonComponent(
                    params,
                    prompt,
                    AIJapaneseVocabularyLessonResponse.class,
                    response -> apiDtoMapper.toVocabularyLessonDTO(response, request.language()));
        } else {
            return generateLessonComponent(
                    params,
                    prompt,
                    AIVocabularyLessonResponse.class,
                    response -> apiDtoMapper.toVocabularyLessonDTO(response, request.language()));
        }
    }

    public Mono<GrammarLessonDTO> generateGrammarLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary) {
        Map<String, Object> params = createBaseParams(request);
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.vocabularies()));

        Resource prompt = getPromptOrThrow(request.language(), PromptType.GRAMMAR);

        return generateLessonComponent(
                params,
                prompt,
                AIGrammarLessonResponse.class,
                response -> apiDtoMapper.toGrammarLessonDTO(response, request.language()));
    }

    public Mono<ConjugationLessonDTO> generateConjugationLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary) {
        Map<String, Object> params = createBaseParams(request);
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.vocabularies()));

        Resource prompt = getPromptOrThrow(request.language(), PromptType.CONJUGATION);

        return generateLessonComponent(
                params,
                prompt,
                AIConjugationLessonResponse.class,
                response -> apiDtoMapper.toConjugationLessonDTO(response, request.language()));
    }

    public Mono<PracticeLessonDTO> generatePracticeLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary, LessonDTO specificLesson) {
        String concept;
        if (specificLesson instanceof  GrammarLessonDTO grammarLesson) {
            concept = grammarLesson.grammarConcept();
        }
        else if (specificLesson instanceof ConjugationLessonDTO conjugationLesson) {
            concept = conjugationLesson.explanation();
        }
        else {
            return Mono.error(new IllegalArgumentException("Unsupported lesson type for practice lesson generation: " + specificLesson.getClass().getName()));
        }

        Map<String, Object> params = createBaseParams(request);
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.vocabularies()));
        params.put("grammarConcept", concept);

        Resource prompt = getPromptOrThrow(request.language(), PromptType.PRACTICE);

        return generateLessonComponent(
                params,
                prompt,
                AIPracticeLessonResponse.class,
                response -> apiDtoMapper.toPracticeLessonDTO(response, request.language()));
    }

    public Mono<ReadingComprehensionLessonDTO> generateReadingComprehensionLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary, LessonDTO specificLesson) {
        String concept;
        if (specificLesson instanceof  GrammarLessonDTO grammarLesson) {
            concept = grammarLesson.grammarConcept();
        }
        else if(specificLesson instanceof ConjugationLessonDTO conjugationLesson) {
            concept = conjugationLesson.explanation();
        }
        else {
            return Mono.error(new IllegalArgumentException("Unsupported lesson type for reading comprehension lesson generation: " + specificLesson.getClass().getName()));
        }

        Map<String, Object> params = createBaseParams(request);
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.vocabularies()));
        params.put("grammarConcept", concept);

        Resource prompt = getPromptOrThrow(request.language(), PromptType.READING_COMPREHENSION);

        return generateLessonComponent(
                params,
                prompt,
                AIReadingComprehensionLessonResponse.class,
                response -> apiDtoMapper.toReadingComprehensionLessonDTO(response, request.language()));
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
        log.info("Generating a {} for topic: {}", componentName, params.get("topic"));
        var outputParser = new BeanOutputConverter<>(apiDtoClass);
        PromptTemplate promptTemplate = createPromptTemplate(promptResource);

        var prompt = promptTemplate.create(params);
        log.debug("Rendered Prompt for {}: {}", componentName, prompt.getContents());
        String language = (String) params.get("language");
        ChatClient chatClient = selectClient(language);

        return chatClient.prompt()
                .user(prompt.getContents())
                .stream().content().collectList()
                .map(list -> String.join("", list).trim())
                .doOnNext(rawResponse -> log.info("Raw AI Response for {}: {}", componentName, rawResponse))
                .map(this::extractAndSanitizeJson)
                .doOnNext(json -> log.debug("Extracted JSON for {}: {}", componentName, json))
                .map(outputParser::convert)
                .map(mapperFunction)
                .doOnNext(mapped -> log.info("Mapped to internal DTO {}: {}", componentName, mapped))
                .doOnError(e -> log.error("Failed to generate or parse AI response for {}.", componentName, e));
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
                .map(WordDTO::nativeWord)
                .collect(Collectors.joining(", "));
    }

    private PromptTemplate createPromptTemplate(Resource resource) {
        try {
            return new PromptTemplate(resource);
        } catch (Exception e) {
            log.error("Failed to create prompt template from resource: {}. Check for syntax errors like unclosed '<' or '>'.", resource.getFilename(), e);
            throw new IllegalArgumentException("Invalid prompt template: " + resource.getFilename(), e);
        }
    }

    /**
     * Extracts a JSON object from a raw string response that may contain conversational text.
     * Sanitize the response string before passing it to the convertor to remove any suspicious/duplicate fields.
     * @param rawResponse The raw string response from the AI.
     * @return A string containing only the JSON object.
     */
    private String extractAndSanitizeJson(String rawResponse) {
        String extractedJson = extractJson(rawResponse);

        try {
            // Convert the json string into a map and then back into a string
            Map<String, Object> objectMap = objectMapper.readValue(extractedJson, new TypeReference<>(){});
            return objectMapper.writeValueAsString(objectMap);
        } catch (Exception e) {
            log.error("Failed to sanitize JSON, returning unsanitized json string: {}", e.getMessage());
            return extractedJson;
        }
    }

    /**
     * Extracts a JSON object from a raw string response that may contain conversational text.
     * @param rawResponse The raw string response from the AI.
     * @return A string containing only the JSON object.
     */
    private String extractJson(String rawResponse) {
        int firstBrace = rawResponse.indexOf('{');
        if (firstBrace == -1) {
            log.warn("AI response did not contain a JSON object. Raw response: {}", rawResponse);
            return "{}";
        }
        //int lastBrace = rawResponse.lastIndexOf('}');
        int braceCount = 0;
        int lastBrace = -1;
        boolean inString = false;

        for (int i = firstBrace; i < rawResponse.length(); i++) {
            char c = rawResponse.charAt(i);

            // Handle entering/exiting strings & ignoring escaped quotes
            if (c == '"' && (i == 0 || rawResponse.charAt(i - 1) != '\\')) {
                inString = !inString;
            }

            if (!inString) {
                if (c == '{') {
                    braceCount++;
                }
                else if (c == '}') {
                    braceCount--;
                }
            }

            if (braceCount == 0) {
                lastBrace = i;
                break;
            }
        }

        if (lastBrace != -1) {
            return rawResponse.substring(firstBrace, lastBrace + 1);
        }
        log.warn("Could not find a balanced JSON object in the AI response. Raw response: {}", rawResponse);
        return "{}";
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
        String modelName = aiConfig.getModelName(language)
                .orElseThrow(() -> new IllegalStateException("AI model name not configured for language: " + language));
        ChatClient client = chatClients.get(modelName);

        if (client == null) {
            log.error("Could not find a ChatClient bean named '{}'. Available beans are: {}", modelName, chatClients.keySet());
            throw new IllegalStateException("AI model client not configured: " + modelName);
        }
        return client;
    }

    private Resource getPromptOrThrow(String language, PromptType promptType) {
        return aiConfig.getPrompt(language, promptType)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Prompt of type '%s' not configured for language: %s", promptType, language)
                ));
    }

    private Map<String, Object> createBaseParams(ChapterGenerationRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("language", request.language());
        params.put("difficulty", request.difficulty());
        params.put("topic", request.topic());
        return params;
    }
}
