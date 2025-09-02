package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.LessonDTO;
import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.entity.lessons.PracticeLesson;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.QuestionRepository;
import com.example.language_learning.requests.ChapterGenerationRequest;
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

    /**
     * Generates a PracticeLesson entity by calling the AI service and mapping the resulting DTO.
     * This service is responsible for creating the lesson entity, but not for persisting it.
     */
    public Mono<PracticeLesson> generateLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabularyLesson, LessonDTO specificLesson) {
        return aiService.generatePracticeLesson(request, vocabularyLesson, specificLesson)
                .map(dto -> (PracticeLesson) dtoMapper.toEntity(dto));
    }

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
