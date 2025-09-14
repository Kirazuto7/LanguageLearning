package com.example.language_learning.utils;

import lombok.RequiredArgsConstructor;
import com.example.language_learning.utils.ReactiveStateMachine.*;
import java.util.List;

/**
 * A factory for creating new instances of a ReactiveStateMachine.
 * This factory is stateless and can be configured as a singleton Spring bean.
 * It holds the blueprint (transitions and initial state) for a specific reactive workflow.
 */
@RequiredArgsConstructor
public class ReactiveStateMachineFactory<S, C> {
    private final List<Transition<S, C>> transitions;
    private final S initialState;

    public ReactiveStateMachine<S, C> createInstance() {
        return new ReactiveStateMachine.Builder<S, C>()
                .initialState(initialState)
                .transitions(transitions)
                .build();
    }
}
