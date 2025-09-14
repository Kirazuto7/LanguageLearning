package com.example.language_learning.services.states;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;
import com.example.language_learning.utils.ReactiveStateMachine.ReactiveTerminalState;

import java.util.Set;

public sealed interface AIGenerationState {
    record IDLE() implements AIGenerationState {}
    record GENERATION(String rawResponse) implements AIGenerationState {}
    record VALIDATION(JsonNode responseNode, Set<ValidationMessage> errors) implements AIGenerationState {}
    record SANITIZING(JsonNode responseNode, Set<ValidationMessage> originalErrors) implements AIGenerationState {}
    record RETRYING(String feedback) implements AIGenerationState {}
    record COMPLETED(Object result) implements AIGenerationState, ReactiveTerminalState {}
    record FAILED(String reason) implements AIGenerationState, ReactiveTerminalState {}
}
