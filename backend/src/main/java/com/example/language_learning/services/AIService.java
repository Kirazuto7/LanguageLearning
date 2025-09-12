package com.example.language_learning.services;

import com.example.language_learning.config.AIConfig;
import com.example.language_learning.dto.api.*;
import com.example.language_learning.dto.lessons.*;
import com.example.language_learning.dto.models.details.*;
import com.example.language_learning.dto.models.*;
import com.example.language_learning.enums.PromptType;
import com.example.language_learning.exceptions.LanguageException;
import com.example.language_learning.mapper.ApiDtoMapper;
import com.example.language_learning.mapper.util.AIResponseSanitizer;
import com.example.language_learning.requests.ChapterGenerationRequest;

import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final AIResponseSanitizer sanitizer;
    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory jsonSchemaFactory;

    public AIService(AIConfig aiConfig, ApiDtoMapper apiDtoMapper, Map<String, ChatClient> chatClients, AIResponseSanitizer sanitizer, ObjectMapper objectMapper, JsonSchemaFactory jsonSchemaFactory) {
        this.aiConfig = aiConfig;
        this.apiDtoMapper = apiDtoMapper;
        this.chatClients = chatClients;
        this.sanitizer = sanitizer;
        this.objectMapper = objectMapper;
        this.jsonSchemaFactory = jsonSchemaFactory;
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
        params.put("promptType", PromptType.PROOFREAD);
        AIConfig.AIPrompt aiPrompt = getPrompt(language, PromptType.PROOFREAD);

        return generateWithRetry(params, aiPrompt, AIProofreadResponse.class, 1)
                .map(apiDtoMapper::toPracticeLessonCheckResponse)
                .doOnNext(mapped -> log.info("Mapped to internal DTO: {}", mapped))
                .doOnError(e -> log.error("Failed to generate or parse AI response.", e));
    }

    /** Chapter Generation Methods **/

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

    private <T_API> Mono<T_API> generateWithRetry(
        Map<String, Object> params,
        AIConfig.AIPrompt aiPrompt,
        Class<T_API> apiDtoClass,
        int attempt
    ) {
        // This is now a convenience method that calls the core `generateWithRetry` logic
        // and is responsible for casting the result back to the specific type.
        JavaType javaType = objectMapper.getTypeFactory().constructType(apiDtoClass);
        return generateWithRetry(params, aiPrompt, javaType, attempt)
                .map(obj -> (T_API) obj); // Unchecked but safe cast
    }

    private Mono<Object> generateWithRetry(Map<String, Object> params, AIConfig.AIPrompt aiPrompt, JavaType apiDtoType, int attempt) {
        // Base case: if we've exceeded the max attempts, fail fast.
        if (attempt > 3) {
            return Mono.error(new IllegalStateException(
                    String.format("AI response validation failed after %d retries for prompt type: %s.", 3, (PromptType) params.get("promptType"))
            ));
        }
        PromptType promptType = (PromptType) params.get("promptType");
        String userMessage = buildUserMessage(aiPrompt, params);
        log.debug("Rendered Prompt for {} (Attempt {}): {}", promptType, attempt, userMessage);

        String language = (String) params.get("language");
        ChatClient chatClient = selectClient(language);

        return chatClient.prompt()
                .user(userMessage)
                .stream()
                .content()
                .collectList()
                .map(list -> String.join("", list).trim())
                .doOnNext(rawResponse -> log.info("Raw AI Response for {} (Attempt {}): {}", promptType, attempt, rawResponse))
                .map(sanitizer::extractAndSanitizeJson)
                .doOnNext(json -> log.debug("Extracted JSON for {} (Attempt {}): {}", promptType, attempt, json))
                .flatMap(jsonString -> {
                    try {
                        JsonNode responseNode = objectMapper.readTree(jsonString);
                        JsonSchema schema = jsonSchemaFactory.getSchema(aiPrompt.schema());
                        Set<ValidationMessage> errors = schema.validate(responseNode);

                        if (errors.isEmpty()) {
                            log.info("Attempt {} for {} passed schema validation.", attempt, promptType);
                            return Mono.just(objectMapper.convertValue(responseNode, apiDtoType)); // Returns Mono<Object>
                        }
                        else { // Validation failed
                            // Validation Error
                            String errorDetails = errors.stream()
                                .map(ValidationMessage::getMessage)
                                .collect(Collectors.joining(", "));

                            log.warn("Attempt {} for {} failed schema validation: {}", attempt, promptType, errorDetails);

                            // Attempt to Sanitize the AI response errors
                            JsonNode fixedNode = sanitizer.sanitizeJsonValidationErrors(responseNode, errors, schema);

                            // Check if anything was fixed
                            if (fixedNode != responseNode) {
                                log.info("Sanitization applied. Re-validating the modified JSON for {}.", promptType);
                                Set<ValidationMessage> newErrors = schema.validate(fixedNode);

                                if (newErrors.isEmpty()) {
                                    log.info("Sanitization successful! {} passed schema validation after fix.", promptType);
                                    return Mono.just(objectMapper.convertValue(fixedNode, apiDtoType));
                                }
                                else {
                                    String newErrorDetails = newErrors.stream()
                                        .map(ValidationMessage::getMessage)
                                        .collect(Collectors.joining(", "));
                                    log.warn("Sanitization attempt failed for {}. New errors: {}", promptType, newErrorDetails);
                                    // Return to retry mechanism
                                }
                            }

                            params.put("invalidJson", jsonString);
                            params.put("validationFeedback", "Your previous response failed schema validation with the following errors: " + errorDetails + ". You MUST fix these errors.");
                            return generateWithRetry(params, aiPrompt, apiDtoType, attempt + 1);
                        }
                    }
                    catch (JsonProcessingException e) {
                        log.warn("Attempt {} for {} failed due to a JSON processing error: {}", attempt, promptType, e.getMessage());
                        params.put("invalidJson", jsonString);
                        params.put("validationFeedback", "Your previous response could not be parsed as valid JSON. It might be malformed or incomplete. You MUST provide a complete and valid JSON object that strictly adheres to the schema.");
                        return generateWithRetry(params, aiPrompt, apiDtoType, attempt + 1);
                    }
                    catch (Exception e) {
                        log.error("An unexpected error occurred during response processing for {} on attempt {}: {}", promptType, attempt, e.getMessage());
                        return Mono.error(e);
                    }
                });

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

        return generateWithRetry(params, aiPrompt, apiDtoClass, 1)
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
        return generateWithRetry(params, aiPrompt, apiDtoType, 1) // This returns Mono<Object>
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

    private String renderPrompt(Resource resource, Map<String, Object> params) {
        try {
            String templateString = resource.getContentAsString(StandardCharsets.UTF_8);
            PromptTemplate promptTemplate = new PromptTemplate(templateString);
            return promptTemplate.render(params);
        } catch (Exception e) {
            log.error("Failed to render prompt template from resource: {}.", resource.getFilename(), e);
            throw new IllegalArgumentException("Invalid prompt template rendering: " + resource.getFilename(), e);
        }
    }

    private String buildUserMessage(AIConfig.AIPrompt aiPrompt, Map<String, Object> params) {
        String instructionContent = renderPrompt(aiPrompt.instruction(), params);

        // Check if this is a retry attempt and add specific feedback
        if (params.containsKey("validationFeedback")) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Your previous response was invalid. You MUST correct it. ");
            messageBuilder.append(params.get("validationFeedback"));
            if (params.containsKey("invalidJson")) {
                messageBuilder.append("\n\nInvalid JSON Response:\n```json\n")
                              .append(params.get("invalidJson"))
                              .append("\n```");
            }
            messageBuilder.append("\n\n--- Original Instructions ---\n");
            messageBuilder.append(instructionContent); // Re-include original instructions
            return messageBuilder.toString();
        }

        // For the initial request, send instructions AND schema.
        JsonNode schemaNode = aiPrompt.schema();
        String schemaContent = (schemaNode != null) ? schemaNode.toPrettyString() : "{}";
        return instructionContent +
            "\n\n## JSON Schema\nYour output MUST conform to the following JSON schema:\n```json\n" +
            schemaContent + "\n```";
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
