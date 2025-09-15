package com.example.language_learning.ai;

import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.ai.contexts.AIGenerationContext;
import com.example.language_learning.ai.states.AIGenerationState;
import com.example.language_learning.config.AIConfig;
import com.example.language_learning.exceptions.LanguageException;
import com.example.language_learning.utils.ReactiveStateMachineFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIEngine {

    private final Map<String, ChatClient> chatClients;
    private final AIConfig aiConfig;
    private final ReactiveStateMachineFactory<AIGenerationState, AIGenerationContext> aiGenerationStateMachineFactory;

    public <T_AI, T_INTERNAL> Mono<T_INTERNAL> generate(AIRequest<T_AI, T_INTERNAL> request) {
        String language = (String) request.getParams().get("language");
        if (language == null || language.isBlank()) {
            return Mono.error(new LanguageException("The 'language' parameter is missing from the AIRequest params."));
        }

        ChatClient chatClient = selectClient(language);
        AIConfig.AIPrompt aiPrompt = aiConfig.getPrompt(language, request.getPromptType());

        AIGenerationContext context = new AIGenerationContext(
            chatClient,
            request.getParams(),
            aiPrompt,
            request.getAiResponseType(),
            3,
            new AtomicInteger(1)
        );

        Mono<T_AI> apiResponseMono = aiGenerationStateMachineFactory.createInstance()
                .runToCompletion(context)
                .onCompletion(AIGenerationState.COMPLETED.class, AIGenerationState.COMPLETED::result)
                .onError(AIGenerationState.FAILED.class, failed -> new IllegalStateException(failed.reason()))
                .asMono()
                .map(obj -> (T_AI) obj);
        return apiResponseMono.map(request.getResponseMapper());
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
            throw new IllegalStateException("AI model client not configured: " + modelName);
        }
        return client;
    }
}
