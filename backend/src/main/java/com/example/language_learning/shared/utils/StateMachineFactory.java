package com.example.language_learning.shared.utils;

import lombok.RequiredArgsConstructor;
import com.example.language_learning.shared.utils.StateMachine.Action;
import java.util.Map;

/**
 * A factory for creating new instances of a {@link StateMachine}.
 * This holds the configured action map and initial state, ensuring each
 * state machine instance starts from a clean, consistent state.
 *
 * @param <S> The base State type.
 * @param <C> The Context type.
 */
@RequiredArgsConstructor
public class StateMachineFactory<S, C> {
    private final Map<Class<? extends S>, Action<S, C>> actionMap;
    private final S initialState;

    public StateMachine<S, C> createInstance() {
        return new StateMachine.Builder<S, C>()
                .initialState(initialState)
                .actionMap(actionMap)
                .build();
    }
}
