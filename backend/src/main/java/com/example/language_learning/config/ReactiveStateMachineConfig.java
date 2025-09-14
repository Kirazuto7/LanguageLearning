package com.example.language_learning.config;

import com.example.language_learning.services.actions.AIGenerationActions;
import com.example.language_learning.services.contexts.AIGenerationContext;
import com.example.language_learning.services.states.AIGenerationState;
import com.example.language_learning.utils.ReactiveStateMachine;
import com.example.language_learning.utils.ReactiveStateMachine.Transition;
import com.example.language_learning.utils.ReactiveStateMachineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ReactiveStateMachineConfig {

    @Bean ReactiveStateMachineFactory<AIGenerationState, AIGenerationContext> aiGenerationStateMachineFactory(AIGenerationActions actions) {
        List<Transition<AIGenerationState, AIGenerationContext>> transitions = new ReactiveStateMachine.GraphBuilder<AIGenerationState, AIGenerationContext>()
                .addTransition(AIGenerationState.IDLE.class, AIGenerationState.GENERATION.class,
                        (s, c) -> true, actions::handleGeneration)
                .addTransition(AIGenerationState.GENERATION.class, AIGenerationState.VALIDATION.class, // Always validate after generating
                        (s, c) -> true, actions::handleValidation)
                // If validation passes (no errors), transition to COMPLETED
                .addTransition(AIGenerationState.VALIDATION.class, AIGenerationState.COMPLETED.class,
                        (s, c) -> ((AIGenerationState.VALIDATION) s).errors().isEmpty(), actions::handleValidationCompletion)
                // If validation fails (has errors), transition to SANITIZING
                .addTransition(AIGenerationState.VALIDATION.class, AIGenerationState.SANITIZING.class,
                        (s, c) -> !((AIGenerationState.VALIDATION) s).errors().isEmpty(), actions::handleSanitization)
                // If sanitization succeeds (no errors), transition to COMPLETED
                .addTransition(AIGenerationState.SANITIZING.class, AIGenerationState.COMPLETED.class,
                        (s, c) -> ((AIGenerationState.SANITIZING) s).originalErrors().isEmpty(), actions::handleSanitizationCompletion)
                // If sanitization fails (has errors), transition to RETRYING
                .addTransition(AIGenerationState.SANITIZING.class, AIGenerationState.RETRYING.class,
                        (s, c) -> !((AIGenerationState.SANITIZING) s).originalErrors().isEmpty(), actions::prepareForRetry)
                // From RETRYING, the handleRetry action will decide whether to loop (IDLE) or fail (FAILED)
                .addTransition(AIGenerationState.RETRYING.class, AIGenerationState.IDLE.class,
                        (s, c) -> true, actions::handleRetry)
                .build();
        return new ReactiveStateMachineFactory<>(transitions, new AIGenerationState.IDLE());
    }
}
