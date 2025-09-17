package com.example.language_learning.ai;

import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.ai.components.AIResponseMapping;
import com.example.language_learning.ai.config.model.AIPrompt;
import com.example.language_learning.ai.contexts.AIGenerationContext;
import com.example.language_learning.ai.states.AIGenerationState;
import com.example.language_learning.ai.config.AIConfig;
import com.example.language_learning.exceptions.LanguageException;
import com.example.language_learning.exceptions.AIEngineException;
import com.example.language_learning.utils.ReactiveStateMachineFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final AIConfig aiConfig;
    private final AIResponseMapperRegistry mapperRegistry;
    private final ReactiveStateMachineFactory<AIGenerationState, AIGenerationContext> aiGenerationStateMachineFactory;

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

        AIGenerationContext context = new AIGenerationContext(
            chatClient,
            request.getParams(),
            aiPrompt,
            aiResponseType,
            3,
            new AtomicInteger(1)
        );

        Mono<T_AI> apiResponseMono = aiGenerationStateMachineFactory.createInstance()
                .runToCompletion(context)
                .onCompletion(AIGenerationState.COMPLETED.class, AIGenerationState.COMPLETED::result)
                .onError(AIGenerationState.FAILED.class, failed -> new AIEngineException(failed.reason()))
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
