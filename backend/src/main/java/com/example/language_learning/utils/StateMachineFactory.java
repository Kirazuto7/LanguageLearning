package com.example.language_learning.utils;

import lombok.RequiredArgsConstructor;
import com.example.language_learning.utils.StateMachine.*;
import java.util.List;

@RequiredArgsConstructor
public class StateMachineFactory<S, C> {
    private final List<Transition<S, C>> transitions;
    private final S initialState;

    public StateMachine<S, C> createInstance() {
        return new StateMachine.Builder<S, C>()
                .initialState(initialState)
                .transitions(transitions)
                .build();
    }
}
