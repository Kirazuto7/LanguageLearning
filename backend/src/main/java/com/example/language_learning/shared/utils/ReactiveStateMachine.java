package com.example.language_learning.shared.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;

@Slf4j
public class ReactiveStateMachine<S, C> {
    @Getter
    private S currentState;
    private final Graph<S, C> graph;

    /**
     * A marker interface for states that should terminate the state machine's execution.
     */
    public interface ReactiveTerminalState {}

    @FunctionalInterface
    public interface Action<S, C> {
        Mono<S> execute(S fromState, C context);

        @SafeVarargs
        static <S, C> Action<S, C> chain(Action<S, C>... actions) {
            return (fromState, context) -> {
                Mono<S> finalState = Mono.just(fromState);
                for (Action<S, C> action : actions) {
                    finalState = finalState.flatMap(state -> {
                        if (state instanceof ReactiveTerminalState) {
                            return Mono.just(state);
                        }
                        return action.execute(state, context);
                    });
                }
                return finalState;
            };
        }
    }

    private ReactiveStateMachine(Map<Class<? extends S>, Action<S, C>> actionMap, S initialState) {
        this.graph = new Graph<>(actionMap);
        this.currentState = initialState;
    }

    public Mono<Boolean> handle(C context) {
        S fromState = currentState;
        return graph.findAction(fromState)
                .map(action -> {
                    log.debug("Executing reactive action from state {}", fromState.getClass().getSimpleName());
                    return action.execute(fromState, context)
                            .map(nextState -> {
                                synchronized (this) {
                                    log.debug("State transition: {} -> {}", fromState.getClass().getSimpleName(), nextState.getClass().getSimpleName());
                                    this.currentState = nextState;
                                }
                                return true; // State transitioned
                            })
                            .defaultIfEmpty(false); // No transition if Mono is empty
                })
                .orElseGet(() -> {
                    log.error("No action found for state {} with context {}", fromState.getClass().getSimpleName(), context);
                    return Mono.just(false); // No action, so no transition
                });
    }

    /**
     * Runs the state machine reactively until a terminal state is reached.
     * @param context The context for the execution.
     * @return A Mono that emits the final, terminal state.
     */
    public ReactiveTerminalOperation<S> runToCompletion(C context) {
        Mono<S> terminalStateMono = Mono.just(this.getCurrentState())
            .expand(state -> {
                if (state instanceof ReactiveTerminalState) {
                    return Mono.empty(); // Stop expanding if we've reached a terminal state
                }
                return this.handle(context).flatMap(transitioned -> {
                    // If a transition happened, continue with the new state. Otherwise, stop.
                    return transitioned ? Mono.fromSupplier(this::getCurrentState) : Mono.<S>empty();
                });
            })
            .last();
        return new ReactiveTerminalOperation<>(terminalStateMono);
    }

    public static class ReactiveTerminalOperation<S> {
        private final Mono<S> terminalStateMono;
        private final Map<Class<? extends S>, Function<S, ? extends Mono<?>>> handlers = new HashMap<>();

        private ReactiveTerminalOperation(Mono<S> terminalStateMono) {
            this.terminalStateMono = terminalStateMono;
        }

        public <T_COMPLETED extends S, R> ReactiveTerminalOperation<S> onCompletion(Class<T_COMPLETED> stateClass, Function<T_COMPLETED, R> resultMapper) {
            handlers.put(stateClass, state -> Mono.defer(() ->
                Mono.just(resultMapper.apply(stateClass.cast(state)))
            ));
            return this;
        }

        public <T_FAILED extends S> ReactiveTerminalOperation<S> onError(Class<T_FAILED> stateClass, Function<T_FAILED, ? extends Throwable> errorMapper) {
            handlers.put(stateClass, state -> Mono.defer(() ->
                Mono.error(errorMapper.apply(stateClass.cast(state)))
            ));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <R> Mono<R> asMono() {
            return terminalStateMono.flatMap(finalState -> {
                Function<S, Mono<R>> handler = (Function<S, Mono<R>>) handlers.get(finalState.getClass());
                if (handler != null) {
                    return handler.apply(finalState);
                }
                return Mono.error(new IllegalStateException("No handler for terminal state: " + finalState.getClass().getSimpleName()));
            });
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

        public ReactiveStateMachine<S, C> build() {
            if (initialState == null || actionMap == null) {
                throw new IllegalStateException("Initial state and action map must be set before building the state machine.");
            }
            return new ReactiveStateMachine<>(actionMap, initialState);
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
