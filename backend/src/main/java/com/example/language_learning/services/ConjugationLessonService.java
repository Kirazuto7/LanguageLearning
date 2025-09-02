package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.entity.lessons.ConjugationLesson;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConjugationLessonService {

    private final DtoMapper dtoMapper;
    private final AIService aiService;

    /**
     * Generates a ConjugationLesson entity by calling the AI service and mapping the resulting DTO.
     * This service is responsible for creating the lesson entity, but not for persisting it.
     */
    public Mono<ConjugationLesson> generateLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabularyLesson) {
        return aiService.generateConjugationLesson(request, vocabularyLesson)
                .map(dto -> (ConjugationLesson) dtoMapper.toEntity(dto));
    }
}
