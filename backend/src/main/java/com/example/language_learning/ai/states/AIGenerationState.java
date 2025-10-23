package com.example.language_learning.ai.states;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;
import com.example.language_learning.shared.utils.ReactiveStateMachine.ReactiveTerminalState;

import java.util.Set;

public sealed interface AIGenerationState {
    record MODERATION() implements AIGenerationState {}
    record GENERATION() implements AIGenerationState {}
    record VALIDATION(String rawResponse) implements AIGenerationState {}
    record SANITIZING(JsonNode responseNode, Set<ValidationMessage> errors) implements AIGenerationState {}
    record RETRYING() implements AIGenerationState {}
    record COMPLETED(Object result) implements AIGenerationState, ReactiveTerminalState {}
    record FAILED(String reason, Throwable cause) implements AIGenerationState, ReactiveTerminalState {}

    static AIGenerationState GENERATION = new GENERATION();
    static AIGenerationState RETRYING = new RETRYING();

    static AIGenerationState MODERATION = new MODERATION();

    public static AIGenerationState VALIDATION(String rawResponse) {
        return new VALIDATION(rawResponse);
    }

    public static AIGenerationState SANITIZING(JsonNode responseNode, Set<ValidationMessage> errors) {
        return new SANITIZING(responseNode, errors);
    }

    public static AIGenerationState COMPLETED(Object result) {
        return new COMPLETED(result);
    }

    public static AIGenerationState FAILED(String reason, Throwable cause) {
        return new FAILED(reason, cause);
    }
}
