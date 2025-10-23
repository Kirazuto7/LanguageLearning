package com.example.language_learning.config;

import com.example.language_learning.ai.actions.AIGenerationActions;
import com.example.language_learning.ai.contexts.AIGenerationContext;
import com.example.language_learning.ai.states.AIGenerationState;
import com.example.language_learning.shared.utils.ReactiveStateMachine;
import com.example.language_learning.shared.utils.ReactiveStateMachineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ReactiveStateMachineConfig {

    @Bean
    ReactiveStateMachineFactory<AIGenerationState, AIGenerationContext> aiGenerationStateMachineFactory(AIGenerationActions actions) {
        var actionMap = new ReactiveStateMachine.GraphBuilder<AIGenerationState, AIGenerationContext>()
                .addState(AIGenerationState.MODERATION.class, actions::handleModeration)
                .addState(AIGenerationState.GENERATION.class, actions::handleGeneration)
                .addState(AIGenerationState.VALIDATION.class, actions::handleValidation)
                .addState(AIGenerationState.SANITIZING.class, actions::handleSanitization)
                .addState(AIGenerationState.RETRYING.class, actions::handleRetry)
                .build();
        return new ReactiveStateMachineFactory<>(actionMap, AIGenerationState.MODERATION);
    }
}
