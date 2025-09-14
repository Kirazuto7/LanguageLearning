package com.example.language_learning.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class StateMachine<S, C>  {
    @Getter
    private S currentState;
    private final Graph<S, C> graph;
    public record Transition<S, C>(Class<? extends S> from, Class<? extends S> to, BiPredicate<S, C> condition, Action<S, C> action) {

        public boolean matches(S fromState, C context) {
            return condition.test(fromState, context);
        }

        public S apply(S fromState, C context) {
            return action.execute(fromState, context);
        }

        public Class<? extends S> getTo() {
            return to;
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

    private StateMachine(List<Transition<S, C>> transitions, S initialState) {
        this.graph = new Graph<>(transitions);
        this.currentState = initialState;
    }

    public synchronized void handle(C context) {
        S fromState = currentState;
        graph.findTransition(fromState, context)
            .ifPresentOrElse(
                transition -> {
                    log.debug("Executing transition from {} to {}", transition.getFromName(), transition.getToName());
                    S nextState = transition.apply(fromState, context);
                    if (nextState != null) {
                        log.debug("State transition: {} -> {}", fromState.getClass().getSimpleName(), nextState.getClass().getSimpleName());
                        currentState = nextState;
                    }
                },
                () -> log.error("No valid transition found for state {} with context {}", fromState, context)
            );
    }

    public TerminalOperation<S> runToCompletion(C context) {
        while (!(currentState instanceof TerminalState)) {
            handle(context);
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

        public StateMachine<S, C> build() {
            if (initialState == null || transitions == null) {
                throw new IllegalStateException("Initial state and transitions must be set before building the state machine.");
            }
            return new StateMachine<>(transitions, initialState);
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
