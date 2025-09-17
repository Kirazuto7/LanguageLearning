package com.example.language_learning.shared.utils;

import lombok.RequiredArgsConstructor;
import com.example.language_learning.shared.utils.ReactiveStateMachine.*;
import java.util.Map;

/**
 * A factory for creating new instances of a ReactiveStateMachine.
 * This factory is stateless and can be configured as a singleton Spring bean.
 * It holds the blueprint (transitions and initial state) for a specific reactive workflow.
 */
@RequiredArgsConstructor
public class ReactiveStateMachineFactory<S, C> {
    private final Map<Class<? extends S>, Action<S, C>> actionMap;
    private final S initialState;

    public ReactiveStateMachine<S, C> createInstance() {
        return new ReactiveStateMachine.Builder<S, C>()
                .initialState(initialState)
                .actionMap(actionMap)
                .build();
    }
}
