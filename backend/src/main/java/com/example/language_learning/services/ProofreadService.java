package com.example.language_learning.services;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.ai.enums.PromptType;
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
    private final AIEngine aiEngine;

    public Mono<PracticeLessonCheckResponse> checkSentence(PracticeLessonCheckRequest request, User user) {

        return Mono.fromCallable(() ->
            questionRepository.findByIdAndUser(request.questionId(), user)
                .orElseThrow(() -> new SecurityException("Question not found or does not belong to the user."))
                .getQuestionText()
            )
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(originalQuestionText -> {
                AIRequest<PracticeLessonCheckResponse> aiRequest = AIRequest.builder()
                        .responseClass(PracticeLessonCheckResponse.class)
                        .promptType(PromptType.PROOFREAD)
                        .language(request.language())
                        .param("question", originalQuestionText)
                        .param("sentence", request.userSentence())
                        .param("difficulty", request.difficulty())
                        .build();

                return aiEngine.generate(aiRequest);
            });
    }
}
