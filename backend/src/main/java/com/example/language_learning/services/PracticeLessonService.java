package com.example.language_learning.services;

import com.example.language_learning.entity.user.User;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.QuestionRepository;
import com.example.language_learning.requests.PracticeLessonCheckRequest;
import com.example.language_learning.responses.PracticeLessonCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PracticeLessonService {

    private final DtoMapper dtoMapper;
    private final AIService aiService;
    private final QuestionRepository questionRepository;

    public Mono<PracticeLessonCheckResponse> checkSentence(PracticeLessonCheckRequest request, User user) {
        return Mono.fromCallable(() ->
                // 1. Fetch the Question data in a blocking-safe manner
                questionRepository.findByIdAndUser(request.questionId(), user)
                .orElseThrow(() -> new SecurityException("Question not found or does not belong to the user."))
        )
        // 2. Request feedback from the AI reactively
        .flatMap(question ->
            aiService.proofRead(question.getQuestionText(), request.userSentence(), request.language(), request.difficulty())
        );
    }
}
