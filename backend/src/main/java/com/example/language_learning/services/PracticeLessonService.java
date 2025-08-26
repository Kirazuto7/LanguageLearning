package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import com.example.language_learning.entity.lessons.PracticeLesson;
import com.example.language_learning.entity.models.Question;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.QuestionRepository;
import com.example.language_learning.requests.PracticeLessonCheckRequest;
import com.example.language_learning.requests.PracticeLessonCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PracticeLessonService {

    private final DtoMapper mapper;
    private final AIService aiService;
    private final QuestionRepository questionRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public PracticeLesson createPracticeLesson(PracticeLessonDTO dto) {
        return (PracticeLesson) mapper.toEntity(dto);
    }

    public PracticeLessonCheckResponse checkSentence(PracticeLessonCheckRequest request) {
        // 1. Fetch the Question data
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + request.questionId()));

        // 2. Request feedback from the AI
        return aiService.proofRead(question.getQuestionText(), request.userSentence(), request.language()).block();
    }
}
