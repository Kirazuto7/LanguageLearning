package com.example.language_learning.services;

import com.example.language_learning.config.AIConfig;
import com.example.language_learning.dto.api.*;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.details.*;
import com.example.language_learning.dto.models.*;
import com.example.language_learning.enums.PromptType;
import com.example.language_learning.exceptions.LanguageException;
import com.example.language_learning.responses.TranslationResponse;
import com.example.language_learning.services.contexts.AIGenerationContext;
import com.example.language_learning.services.states.AIGenerationState;
import com.example.language_learning.utils.ReactiveStateMachineFactory;
import com.example.language_learning.mapper.ApiDtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;

import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final AIConfig aiConfig;
    private final ObjectMapper objectMapper;
    private final ReactiveStateMachineFactory<AIGenerationState, AIGenerationContext> aiGenerationStateMachineFactory;

    public AIService(AIConfig aiConfig, ApiDtoMapper apiDtoMapper, Map<String, ChatClient> chatClients, ObjectMapper objectMapper, ReactiveStateMachineFactory<AIGenerationState, AIGenerationContext> aiGenerationStateMachineFactory) {
        this.aiConfig = aiConfig;
        this.apiDtoMapper = apiDtoMapper;
        this.chatClients = chatClients;
        this.objectMapper = objectMapper;
        this.aiGenerationStateMachineFactory = aiGenerationStateMachineFactory;
        log.info("--- Verifying Injected ChatClients ---");
        log.info("Found {} ChatClient bean(s):", chatClients.size());
        chatClients.keySet().forEach(key -> log.info(" -> Bean name: '{}'", key));
        log.info("--------------------------------------");
    }

    /** Translation Method(s) **/
    public Mono<TranslationResponse> translate(String text, String sourceLanguage) {
        Map<String, Object> params = new HashMap<>();
        params.put("language", sourceLanguage);
        params.put("textToTranslate", text);
        params.put("sourceLanguage", sourceLanguage);
        params.put("promptType", PromptType.TRANSLATE);

        AIConfig.AIPrompt aiPrompt = getPrompt(sourceLanguage, PromptType.TRANSLATE);

        return this.generateAndValidate(params, aiPrompt, AITranslationResponse.class)
                .map(apiDtoMapper::toTranslationResponse)
                .doOnNext(mapped -> log.info("Mapped to internal DTO: {}", mapped))
                .doOnError(e -> log.error("Failed to generate or parse AI response.", e));
    }

    /** Practice Lesson Method(s) **/
    public Mono<PracticeLessonCheckResponse> proofRead(String originalQuestion, String userSentence, String language, String difficulty) {
        log.info("Proofreading question: {}", originalQuestion);

        Map<String, Object> params = new HashMap<>();
        params.put("language", language);
        params.put("question", originalQuestion);
        params.put("sentence", userSentence);
        params.put("difficulty", difficulty);
        params.put("promptType", PromptType.PROOFREAD);
        AIConfig.AIPrompt aiPrompt = getPrompt(language, PromptType.PROOFREAD);

        return generateAndValidate(params, aiPrompt, AIProofreadResponse.class)
                .map(apiDtoMapper::toPracticeLessonCheckResponse)
                .doOnNext(mapped -> log.info("Mapped to internal DTO: {}", mapped))
                .doOnError(e -> log.error("Failed to generate or parse AI response.", e));
    }

    /** Chapter Generation Method(s) **/

    public Mono<ChapterMetadataDTO> generateChapterMetadata(ChapterGenerationRequest request) {
        Map<String, Object> params = createBaseParams(request);

        AIConfig.AIPrompt prompt = getPrompt(request.language(), PromptType.METADATA);

        return generateLessonComponent(
                params,
                prompt,
                AIChapterMetadataResponse.class,
                apiResponse -> apiDtoMapper.toChapterMetadataDTO(apiResponse, request.topic()),
                PromptType.METADATA);
    }

    public Mono<VocabularyLessonDTO> generateVocabularyLesson(ChapterGenerationRequest request, ChapterMetadataDTO metadata) {
        Map<String, Object> params = createBaseParams(request);
        params.put("chapterTitle", metadata.title());
        params.put("nativeChapterTitle", metadata.nativeTitle());

        AIConfig.AIPrompt prompt = aiConfig.getPrompt(request.language(), PromptType.VOCABULARY);

        Class<?> itemDtoClass = aiConfig.getVocabularyItemDtoClass(request.language());
        if (itemDtoClass == null) {
            return Mono.error(new LanguageException("Unsupported language for vocabulary generation: " + request.language()));
        }

        JavaType responseType = objectMapper.getTypeFactory()
                .constructParametricType(AIVocabularyLessonResponse.class, itemDtoClass);

        return generateLessonComponent(
            params,
            prompt,
            responseType,
            (AIVocabularyLessonResponse<?> response) -> apiDtoMapper.toVocabularyLessonDTO(response, request.language()),
            PromptType.VOCABULARY
        );
    }

    public Mono<GrammarLessonDTO> generateGrammarLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary) {
        Map<String, Object> params = createBaseParams(request);
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.vocabularies()));

        AIConfig.AIPrompt prompt = aiConfig.getPrompt(request.language(), PromptType.GRAMMAR);

        return generateLessonComponent(
                params,
                prompt,
                AIGrammarLessonResponse.class,
                response -> apiDtoMapper.toGrammarLessonDTO(response, request.language()),
                PromptType.GRAMMAR);
    }

    public Mono<ConjugationLessonDTO> generateConjugationLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabulary) {
        Map<String, Object> params = createBaseParams(request);
        params.put("vocabulary", formatVocabularyForPrompt(vocabulary.vocabularies()));

        AIConfig.AIPrompt prompt = aiConfig.getPrompt(request.language(), PromptType.CONJUGATION);

        return generateLessonComponent(
                params,
                prompt,
                AIConjugationLessonResponse.class,
                response -> apiDtoMapper.toConjugationLessonDTO(response, request.language()),
                PromptType.CONJUGATION);
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

        AIConfig.AIPrompt prompt = aiConfig.getPrompt(request.language(), PromptType.PRACTICE);

        return generateLessonComponent(
                params,
                prompt,
                AIPracticeLessonResponse.class,
                response -> apiDtoMapper.toPracticeLessonDTO(response, request.language()),
                PromptType.PRACTICE);
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

        AIConfig.AIPrompt prompt = aiConfig.getPrompt(request.language(), PromptType.READING_COMPREHENSION);

        return generateLessonComponent(
                params,
                prompt,
                AIReadingComprehensionLessonResponse.class,
                response -> apiDtoMapper.toReadingComprehensionLessonDTO(response, request.language()),
                PromptType.READING_COMPREHENSION);
    }

    private <T_API> Mono<T_API> generateAndValidate(Map<String, Object> params, AIConfig.AIPrompt aiPrompt, Class<T_API> apiDtoClass) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(apiDtoClass);
        return generateAndValidate(params, aiPrompt, javaType).map(obj -> (T_API) obj);
    }

    private Mono<Object> generateAndValidate(Map<String, Object> params, AIConfig.AIPrompt aiPrompt, JavaType apiDtoType) {
        Object langObj = params.get("language");
        if (langObj == null || !(langObj instanceof  String) || ((String) langObj).isBlank()) {
            String errorMessage = "The 'language' parameter is missing or invalid in the AI service call. This is a required parameter for selecting the correct AI model and prompt.";
            return Mono.error(new LanguageException(errorMessage));
        }

        String language = (String) params.get("language");
        ChatClient chatClient = selectClient(language);
        int maxRetries = 3;

        AIGenerationContext context = new AIGenerationContext(
                chatClient,
                params,
                aiPrompt,
                apiDtoType,
                maxRetries,
                new AtomicInteger(1)
        );

        return aiGenerationStateMachineFactory.createInstance()
                .runToCompletion(context)
                .onCompletion(AIGenerationState.COMPLETED.class, AIGenerationState.COMPLETED::result)
                .onError(AIGenerationState.FAILED.class, failed -> new IllegalStateException(failed.reason()))
                .asMono();
    }
    /**
     * A generic method to generate any lesson component.
     * It first parses the AI response into a dedicated API DTO, then maps it to the application's internal DTO.
     *
     * @param params         A map of parameters to be injected into the prompt template.
     * @param aiPrompt       The prompt object containing the instruction and schema.
     * @param apiDtoClass    The class of the API-specific DTO to parse into (e.g., AIGrammarLessonResponse.class).
     * @param mapperFunction The function to map from the API DTO to the internal DTO (e.g., apiDtoMapper::toGrammarLessonDTO).
     * @param <T_API>        The type of the API DTO.
     * @param <T_INTERNAL> The type of the internal application DTO.
     * @return A Mono containing the final, internal DTO.
     */
    private <T_API, T_INTERNAL> Mono<T_INTERNAL> generateLessonComponent(
            Map<String, Object> params,
            AIConfig.AIPrompt aiPrompt,
            Class<T_API> apiDtoClass,
            Function<T_API, T_INTERNAL> mapperFunction,
            PromptType promptType)
    {
        log.info("Generating a {} for topic: {}", promptType, params.get("topic"));

        params.put("promptType", promptType);

        return generateAndValidate(params, aiPrompt, apiDtoClass)
                .map(mapperFunction)
                .doOnNext(mapped -> log.info("Mapped to internal DTO {}: {}", promptType, mapped))
                .doOnError(e -> log.error("Failed to generate or parse AI response for {}.", promptType, e));
    }

    private <T_API, T_INTERNAL> Mono<T_INTERNAL> generateLessonComponent(
            Map<String, Object> params,
            AIConfig.AIPrompt aiPrompt,
            JavaType apiDtoType,
            Function<T_API, T_INTERNAL> mapperFunction,
            PromptType promptType)
    {
        log.info("Generating a {} for topic: {}", promptType, params.get("topic"));
        params.put("promptType", promptType);
        return generateAndValidate(params, aiPrompt, apiDtoType) // This returns Mono<Object>
                .map(obj -> (T_API) obj) // We must cast the object before applying the typed mapper function.
                .map(mapperFunction)
                .doOnNext(mapped -> log.info("Mapped to internal DTO {}: {}", promptType, mapped))
                .doOnError(e -> log.error("Failed to generate or parse AI response for {}.", promptType, e));
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
                .map(wordDto -> {
                    WordDetailsDTO details = wordDto.details();
                    if (details == null) return "";

                    return switch (details) {
                        case JapaneseWordDetailsDTO j -> {
                            if (j.kanji() != null && !j.kanji().isBlank()) yield j.kanji();
                            if (j.hiragana() != null && !j.hiragana().isBlank()) yield j.hiragana();
                            yield j.katakana();
                        }
                        case KoreanWordDetailsDTO k -> k.hangul();
                        case ChineseWordDetailsDTO c -> c.simplified();
                        case ThaiWordDetailsDTO t -> t.thaiScript();
                        case ItalianWordDetailsDTO i -> i.lemma();
                        case SpanishWordDetailsDTO s -> s.lemma();
                        case FrenchWordDetailsDTO f -> f.lemma();
                        case GermanWordDetailsDTO g -> g.lemma();
                        default -> "";
                    };
                })
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(", "));
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
        String modelName = aiConfig.getModelName(language);
        ChatClient client = chatClients.get(modelName);

        if (client == null) {
            log.error("Could not find a ChatClient bean named '{}'. Available beans are: {}", modelName, chatClients.keySet());
            throw new IllegalStateException("AI model client not configured: " + modelName);
        }
        return client;
    }

    private AIConfig.AIPrompt getPrompt(String language, PromptType promptType) {
        return aiConfig.getPrompt(language, promptType);
    }

    private Map<String, Object> createBaseParams(ChapterGenerationRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("language", request.language());
        params.put("difficulty", request.difficulty());
        params.put("topic", request.topic());
        return params;
    }
}
