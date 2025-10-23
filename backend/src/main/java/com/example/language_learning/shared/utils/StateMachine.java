package com.example.language_learning.shared.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class StateMachine<S, C>  {
    @Getter
    private S currentState;
    private final Graph<S, C> graph;

    /**
     * A marker interface for states that should terminate the state machine's execution.
     */
    public interface TerminalState {}

    @FunctionalInterface
    public interface Action<S, C> {
        S execute(S fromState, C context);

        /**
         * A helper method to chain multiple actions into a single, composite action.
         * The output state of one action becomes the input state for the next, turning
         * a sequence of actions into a single, cohesive workflow.
         *
         * @param actions The sequence of actions to chain in order.
         * @return A single Action that executes the provided actions in order.
         */
        @SafeVarargs
        static <S, C> Action<S, C> chain(Action<S, C>... actions) {
            return (fromState, context) -> {
                S currentState = fromState;
                for (Action<S, C> action : actions) {
                    currentState = action.execute(currentState, context);
                }
                return currentState;
            };
        }
    }

    private StateMachine(Map<Class<? extends S>, Action<S, C>> actionMap, S initialState) {
        this.graph = new Graph<>(actionMap);
        this.currentState = initialState;
    }

    public synchronized boolean handle(C context) {
        S fromState = currentState;
        return graph.findAction(fromState)
            .map(
                action -> {
                    log.debug("Executing action from state {}", fromState.getClass().getSimpleName());
                    S nextState = action.execute(fromState, context);
                    if (nextState != null) {
                        log.debug("State transition: {} -> {}", fromState.getClass().getSimpleName(), nextState.getClass().getSimpleName());
                        currentState = nextState;
                        return true; // State transitioned
                    }
                    return false; // No transition occurred
                }
            ).orElseGet(() -> {
                log.error("No action found for state {} with context {}", fromState.getClass().getSimpleName(), context);
                return false; // No action, so no transition
            });
    }

    public TerminalOperation<S> runToCompletion(C context) {
        while (!(currentState instanceof TerminalState) && handle(context)) {
            // The loop continues as long as we are not in a terminal state AND a state transition occurred.
        }
        return new TerminalOperation<>(currentState);
    }

    public static class TerminalOperation<S> {
        private final S terminalState;
        private final Map<Class<? extends S>, Consumer<S>> handlers = new HashMap<>();

        private TerminalOperation(S terminalState) {
            this.terminalState = terminalState;
        }

        public <T_COMPLETED extends S>TerminalOperation<S> onCompletion(Class<T_COMPLETED> stateClass, Consumer<T_COMPLETED> handler) {
            handlers.put(stateClass, state -> handler.accept(stateClass.cast(state)));
            return this;
        }

        public <T_FAILED extends S>TerminalOperation<S> onError(Class<T_FAILED> stateClass, Consumer<T_FAILED> handler) {
            handlers.put(stateClass, state -> handler.accept(stateClass.cast(state)));
            return this;
        }

        public void execute() {
            Consumer<S> handler = handlers.get(terminalState.getClass());
            if (handler != null) {
                handler.accept(terminalState);
            }
            else {
                log.warn("No terminal handler found for state: {}", terminalState.getClass().getSimpleName());
            }
        }
    }

    private static class Graph<S, C> {
        private final Map<Class<? extends S>, Action<S, C>> actionMap;

        public Graph(Map<Class<? extends S>, Action<S, C>> actionMap) {
            this.actionMap = actionMap;
        }

        public Optional<Action<S, C>> findAction(S fromState) {
            return Optional.ofNullable(actionMap.get(fromState.getClass()));
        }
    }

    static class Builder<S, C> {
        private Map<Class<? extends S>, Action<S, C>> actionMap;
        private S initialState;

        public Builder<S, C> actionMap(Map<Class<? extends S>, Action<S, C>> actionMap) {
            this.actionMap = actionMap;
            return this;
        }

        public Builder<S, C> initialState(S initialState) {
            this.initialState = initialState;
            return this;
        }

        public StateMachine<S, C> build() {
            if (initialState == null || actionMap == null) {
                throw new IllegalStateException("Initial state and action map must be set before building the state machine.");
            }
            return new StateMachine<>(actionMap, initialState);
        }
    }

    public static class GraphBuilder<S, C> {
        private final Map<Class<? extends S>, Action<S, C>> actionMap = new HashMap<>();

        public GraphBuilder<S, C> addState(Class<? extends S> state, Action<S, C> action) {
            this.actionMap.put(state, action);
            return this;
        }

        public Map<Class<? extends S>, Action<S, C>> build() {
            return Map.copyOf(actionMap);
        }
    }
}
