package com.example.language_learning.services.states;


import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.example.language_learning.utils.ReactiveStateMachine;

public sealed interface ProofreadState {
    record IDLE() implements ProofreadState {}
    record FETCHING_QUESTION(String originalQuestionText) implements ProofreadState {}
    record CALLING_AI(PracticeLessonCheckResponse response) implements ProofreadState {}
    record COMPLETED(PracticeLessonCheckResponse response) implements ProofreadState, ReactiveStateMachine.ReactiveTerminalState {}
    record FAILED(String reason) implements ProofreadState, ReactiveStateMachine.ReactiveTerminalState {}
}
