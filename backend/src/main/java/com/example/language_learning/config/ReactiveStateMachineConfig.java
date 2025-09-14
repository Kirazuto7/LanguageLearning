package com.example.language_learning.config;

import com.example.language_learning.services.actions.ProofreadActions;
import com.example.language_learning.services.contexts.ProofreadContext;
import com.example.language_learning.services.states.ProofreadState;
import com.example.language_learning.utils.ReactiveStateMachine;
import com.example.language_learning.utils.ReactiveStateMachine.Transition;
import com.example.language_learning.utils.ReactiveStateMachineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ReactiveStateMachineConfig {

    @Bean
    public ReactiveStateMachineFactory<ProofreadState, ProofreadContext> proofreadStateMachineFactory(ProofreadActions actions) {
        ReactiveStateMachine.Action<ProofreadState, ProofreadContext> proofreadWorkFlow = ReactiveStateMachine.Action.chain(
                actions::handleFetchingQuestion,
                actions::handleCallingAI,
                actions::handleCompletion
        );

        List<Transition<ProofreadState, ProofreadContext>> transitions =  new ReactiveStateMachine.GraphBuilder<ProofreadState, ProofreadContext>()
                .addTransition(ProofreadState.IDLE.class, ProofreadState.COMPLETED.class,
                        (s, c) -> true, proofreadWorkFlow)
                .build();
        return new ReactiveStateMachineFactory<>(transitions, new ProofreadState.IDLE());
    }
}
