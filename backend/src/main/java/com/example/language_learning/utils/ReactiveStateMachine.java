package com.example.language_learning.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ReactiveStateMachine<S, C> {
    @Getter
    private S currentState;
    private final Graph<S, C> graph;

    public record Transition<S, C>(Class<? extends S> from, Class<? extends S> to, BiPredicate<S, C> condition, Action<S, C> action) {
        public boolean matches(S fromState, C context) {
            return condition.test(fromState, context);
        }

        public Mono<S> apply(S fromState, C context) {
            return action.execute(fromState, context);
        }

        public String getFromName() {
            return from.getSimpleName();
        }

        public String getToName() {
            return to.getSimpleName();
        }
    }

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

    private ReactiveStateMachine(List<Transition<S, C>> transitions, S initialState) {
        this.graph = new Graph<>(transitions);
        this.currentState = initialState;
    }

    public Mono<Void> handle(C context) {
        S fromState = currentState;
        return graph.findTransition(fromState, context)
                .map(transition -> {
                    log.debug("Executing reactive transition from {} to {}", transition.getFromName(), transition.getToName());
                    return transition.apply(fromState, context)
                            .doOnNext(nextState -> {
                                synchronized (this) {
                                    log.debug("State transition: {} -> {}", fromState.getClass().getSimpleName(), nextState.getClass().getSimpleName());
                                    this.currentState = nextState;
                                }
                            }).then();
                })
                .orElseGet(() -> {
                    log.error("No valid transition found for state {} with context {}", fromState, context);
                    return Mono.error(new IllegalStateException("No valid transition found for state " + fromState.getClass().getSimpleName()));
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
                return this.handle(context).then(Mono.fromSupplier(this::getCurrentState));
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
        private final Map<Class<? extends S>, List<Transition<S, C>>> transitionMap;

        public Graph(List<Transition<S, C>> transitions) {
            this.transitionMap = transitions.stream()
                    .collect(Collectors.groupingBy(Transition::from));
        }

        public Optional<Transition<S, C>> findTransition(S fromState, C context) {
            List<Transition<S, C>> possibleTransitions = transitionMap.get(fromState.getClass());
            if (possibleTransitions == null) {
                return Optional.empty();
            }
            return possibleTransitions.stream()
                    .filter(transition -> transition.matches(fromState, context))
                    .findFirst();
        }
    }

    public static class Builder<S, C> {
        private List<Transition<S, C>> transitions;
        private S initialState;

        public Builder<S, C> transitions(List<Transition<S, C>> transitions) {
            this.transitions = transitions;
            return this;
        }

        public Builder<S, C> initialState(S initialState) {
            this.initialState = initialState;
            return this;
        }

        public ReactiveStateMachine<S, C> build() {
            if (initialState == null || transitions == null) {
                throw new IllegalStateException("Initial state and transitions must be set before building the state machine.");
            }
            return new ReactiveStateMachine<>(transitions, initialState);
        }
    }

    public static class GraphBuilder<S, C> {
        private final List<Transition<S, C>> transitions = new ArrayList<>();

        public GraphBuilder<S, C> addTransition(Class<? extends S> from, Class<? extends S> to, BiPredicate<S, C> condition, Action<S, C> action) {
            this.transitions.add(new Transition<>(from, to, condition, action));
            return this;
        }

        public List<Transition<S, C>> build() {
            return List.copyOf(transitions);
        }
    }
}
