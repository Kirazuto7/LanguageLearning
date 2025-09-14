package com.example.language_learning.services;

import com.example.language_learning.repositories.QuestionRepository;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.example.language_learning.requests.PracticeLessonCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProofreadService {

    private final QuestionRepository questionRepository;
    private final AIService aiService;

    public Mono<PracticeLessonCheckResponse> checkSentence(PracticeLessonCheckRequest request, User user) {

        // Step 1: Fetch the question text (blocking I/O) and offload it to a background thread.
        return Mono.fromCallable(() ->
            questionRepository.findByIdAndUser(request.questionId(), user)
                .orElseThrow(() -> new SecurityException("Question not found or does not belong to the user."))
                .getQuestionText()
            )
            .subscribeOn(Schedulers.boundedElastic())
            // Step 2: Use the fetched text to call the AI service.
            .flatMap(originalQuestionText ->
                aiService.proofRead(
                    originalQuestionText,
                    request.userSentence(),
                    request.language(),
                    request.difficulty()
                )
            );
    }
}
