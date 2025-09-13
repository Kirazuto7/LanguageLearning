package com.example.language_learning.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Slf4j
public class StateMachine<S, C>  {
    @Getter
    private S currentState;
    private final Graph<S, C> graph;
    public record Transition<S, C>(Class<? extends S> from, Class<? extends S> to, BiPredicate<S, C> condition, Action<S, C> action) {}

    @FunctionalInterface
    public interface Action<S, C> {
        S execute(S fromState, C context);
    }

    public StateMachine(List<Transition<S, C>> transitions, S initialState) {
        this.graph = new Graph<>(transitions);
        this.currentState = initialState;
    }

    public synchronized void handle(C context) {
        S fromState = currentState;
        graph.findTransition(fromState, context)
            .ifPresentOrElse(
                transition -> {
                    log.debug("State transition: {} -> {}", fromState.getClass().getSimpleName(), transition.to().getSimpleName());
                    S nextState = transition.action().execute(fromState, context);
                    currentState = nextState;
                },
                () -> log.error("No valid transition found for state {} with context {}", fromState, context)
            );
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
                    .filter(transition -> transition.condition().test(fromState, context))
                    .findFirst();
        }
    }
}
