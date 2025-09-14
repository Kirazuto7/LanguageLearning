package com.example.language_learning.config;

import com.example.language_learning.services.actions.AIGenerationActions;
import com.example.language_learning.services.contexts.AIGenerationContext;
import com.example.language_learning.services.states.AIGenerationState;
import com.example.language_learning.utils.ReactiveStateMachine;
import com.example.language_learning.utils.ReactiveStateMachineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ReactiveStateMachineConfig {

    @Bean
    ReactiveStateMachineFactory<AIGenerationState, AIGenerationContext> aiGenerationStateMachineFactory(AIGenerationActions actions) {
        var actionMap = new ReactiveStateMachine.GraphBuilder<AIGenerationState, AIGenerationContext>()
                .addState(AIGenerationState.GENERATION.class, actions::handleGeneration)
                .addState(AIGenerationState.VALIDATION.class, actions::handleValidation)
                .addState(AIGenerationState.SANITIZING.class, actions::handleSanitization)
                .addState(AIGenerationState.RETRYING.class, actions::handleRetry)
                .build();
        return new ReactiveStateMachineFactory<>(actionMap, AIGenerationState.GENERATION);
    }
}
