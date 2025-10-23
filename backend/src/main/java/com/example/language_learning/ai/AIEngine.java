package com.example.language_learning.ai;

import com.example.language_learning.ai.components.AIImageRequest;
import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.ai.components.AIResponseMapping;
import com.example.language_learning.ai.config.model.AIPrompt;
import com.example.language_learning.ai.contexts.AIGenerationContext;
import com.example.language_learning.ai.states.AIGenerationState;
import com.example.language_learning.ai.config.AIConfig;
import com.example.language_learning.shared.dtos.images.GeneratedImageDTO;
import com.example.language_learning.shared.exceptions.LanguageException;
import com.example.language_learning.shared.exceptions.AIEngineException;
import com.example.language_learning.shared.services.ImageService;
import com.example.language_learning.shared.utils.ReactiveStateMachineFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * The central "smart" engine for all AI interactions.
 * This service takes a simple {@link AIRequest}, which describes the user's intent,
 * and orchestrates the entire generation and mapping process. It uses the {@link AIResponseMapperRegistry}
 * to dynamically look up the correct AI response type and mapping function, providing a fully
 * type-safe, extensible, and clean public API for the rest of the application.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AIEngine {

    private final Map<String, ChatClient> chatClients;
    private final ImageModel imageModel;
    private final AIConfig aiConfig;
    private final AIResponseMapperRegistry mapperRegistry;
    private final ReactiveStateMachineFactory<AIGenerationState, AIGenerationContext> aiGenerationStateMachineFactory;
    private final ImageService imageService;
    @Value("${spring.ai.max-retries}")
    private int maxRetries;

    /**
     * Generates images based on a list of text prompts provided in the request.
     * This method bypasses the text-generation state machine and uses the ImageModel directly.
     *
     * @param request The AI request. The parameters map must contain a key "prompts" with a List<String> value.
     * @param <T_INTERNAL> The expected return type, which must be compatible with List<String>.
     * @return A Mono emitting a list of Base64 encoded image strings.
     */
    public<T_INTERNAL> Mono<T_INTERNAL> generateImages(AIImageRequest<T_INTERNAL> request) {
        Object contextObject = request.getParams().get("context");
        if (!(contextObject instanceof String imageContext) || imageContext.isBlank()) {
            return Mono.error(new AIEngineException("The 'context' parameter must be a non-blank String."));
        }

        List<String> textPrompts = Arrays.stream(imageContext.split("\n"))
                .filter(s -> !s.isBlank())
                .toList();

        if (textPrompts.isEmpty()) {
            return Mono.just((T_INTERNAL) new GeneratedImageDTO(Map.of(),imageContext));
        }

        log.info("Generating {} images in parallel...", textPrompts.size());

        return Flux.fromIterable(textPrompts)
                .flatMap(prompt -> Mono.fromCallable(() -> {
                    ImagePrompt imagePrompt = new ImagePrompt(prompt);
                    String base64Image = imageModel.call(imagePrompt).getResult().getOutput().getB64Json();
                    String url = imageService.saveImageFromBase64(base64Image);
                    return Map.entry(prompt, url);
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue))
                .doOnSuccess(urlsByPrompt -> log.info("Successfully retrieved and saved {} images.", urlsByPrompt.size()))
                .map(urlsByPrompt -> (T_INTERNAL) new GeneratedImageDTO(urlsByPrompt, imageContext));
    }

    /**
     * The main public entry point for the AIEngine.
     * It takes a simple request and returns a strongly-typed Mono of the final internal DTO.
     *
     * @param request The AI request describing the user's intent.
     * @param <T_INTERNAL> The final internal DTO type the caller expects.
     * @return A {@link Mono} that emits the final, mapped DTO.
     */
    public <T_INTERNAL> Mono<T_INTERNAL> generate(AIRequest<T_INTERNAL> request) {
        AIResponseMapping<?, T_INTERNAL> mapping = mapperRegistry.get(request.getPromptType());
        if (mapping == null) {
            return Mono.error(new AIEngineException("No mapper registered for prompt type: " + request.getPromptType()));
        }
        return generateAndMap(request, mapping);
    }

    /**
     * A private, generic helper method that ensures end-to-end type safety.
     * By having this intermediate method with both {@code T_AI} and {@code T_INTERNAL} generic parameters,
     * it allows the compiler to correctly link the type of the {@code Mono} returned by the state machine
     * with the input type of the mapper function from the registry. This is a standard pattern to
     * work around Java's type erasure and avoid unsafe casting in the public-facing API.
     *
     * @param request The original AI request.
     * @param mapping The mapping strategy retrieved from the registry.
     */
    private <T_AI, T_INTERNAL> Mono<T_INTERNAL> generateAndMap(AIRequest<T_INTERNAL> request, AIResponseMapping<T_AI, T_INTERNAL> mapping) {
        String language = (String) request.getParams().get("language");
        if (language == null || language.isBlank()) {
            return Mono.error(new LanguageException("The 'language' parameter is missing from the AIRequest params."));
        }

        ChatClient chatClient = selectClient(language);
        AIPrompt aiPrompt = aiConfig.getPrompt(language, request.getPromptType());
        var aiResponseType = mapping.javaTypeProvider().apply(request.getParams());

        Map<String, Object> contextParams = new HashMap<>(request.getParams());
        contextParams.put("promptType", request.getPromptType());

        AIGenerationContext context = new AIGenerationContext(
            chatClient,
            contextParams,
            aiPrompt,
            aiResponseType,
            maxRetries,
            new AtomicInteger(1),
            request.isWithModeration(),
            language
        );

        Mono<T_AI> apiResponseMono = aiGenerationStateMachineFactory.createInstance()
                .runToCompletion(context)
                .onCompletion(AIGenerationState.COMPLETED.class, AIGenerationState.COMPLETED::result)
                .onError(AIGenerationState.FAILED.class, failed -> {
                    log.info("DIAGNOSTIC: AIEngine - Creating AIEngineException.");
                    log.error("AI generation failed for prompt type '{}'. Reason: {}", request.getPromptType(), failed.reason(), failed.cause());
                    return new AIEngineException(failed.reason(), failed.cause());
                })
                .asMono()
                .map(obj -> (T_AI) obj);
        return apiResponseMono.map(response -> mapping.mapper().apply(response, request.getParams()));
    }

    /**
     * Selects a ChatClient based on the chosen language setting.
     *
     * @param language The language setting used to select the ChatClient to use.
     * @return The Chat client for the selected language.
     */
    private ChatClient selectClient(String language) {
        if(language == null || language.isBlank()) {
            throw new LanguageException("Language cannot be null or empty.");
        }
        String modelName = aiConfig.getModelName(language);
        ChatClient client = chatClients.get(modelName);

        if (client == null) {
            log.error("Could not find a ChatClient bean named '{}'. Available beans are: {}", modelName, chatClients.keySet());
            throw new AIEngineException("AI model client not configured: " + modelName);
        }
        return client;
    }
}
