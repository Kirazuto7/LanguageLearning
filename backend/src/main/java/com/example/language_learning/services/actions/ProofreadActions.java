package com.example.language_learning.services.actions;

import com.example.language_learning.repositories.QuestionRepository;
import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.example.language_learning.services.AIService;
import com.example.language_learning.services.contexts.ProofreadContext;
import com.example.language_learning.services.states.ProofreadState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class ProofreadActions {

    private final AIService aiService;
    private final QuestionRepository questionRepository;

    public Mono<ProofreadState> handleFetchingQuestion(ProofreadState fromState, ProofreadContext context) {
        return Mono.fromCallable(() -> {
            try {
                String originalQuestionText = questionRepository.findByIdAndUser(context.request().questionId(), context.user())
                        .orElseThrow(() -> new SecurityException("Question not found or does not belong to the user."))
                        .getQuestionText();
                return new ProofreadState.FETCHING_QUESTION(originalQuestionText);
            }
            catch (Exception e) {
                return new ProofreadState.FAILED(e.getMessage());
            }
        })
        .cast(ProofreadState.class)
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(e -> Mono.just(new ProofreadState.FAILED(e.getMessage())));
    }

    public Mono<ProofreadState> handleCallingAI(ProofreadState fromState, ProofreadContext context) {
        try {
            String originalQuestionText = ((ProofreadState.FETCHING_QUESTION) fromState).originalQuestionText();

            return aiService.proofRead(
                    originalQuestionText,
                    context.request().userSentence(),
                    context.request().language(),
                    context.request().difficulty()
            )
            .map(ProofreadState.CALLING_AI::new);
        }
        catch (Exception e) {
            return Mono.just(new ProofreadState.FAILED(e.getMessage()));
        }
    }

    public Mono<ProofreadState> handleCompletion(ProofreadState fromState, ProofreadContext context) {
        return Mono.fromSupplier(() -> {
            try {
                PracticeLessonCheckResponse practiceLessonCheckResponse = ((ProofreadState.CALLING_AI) fromState).response();
                return new ProofreadState.COMPLETED(practiceLessonCheckResponse);
            }
            catch (Exception e) {
                return new ProofreadState.FAILED(e.getMessage());
            }
        });
    }
}
