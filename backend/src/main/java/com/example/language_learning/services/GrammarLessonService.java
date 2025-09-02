package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.entity.lessons.GrammarLesson;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GrammarLessonService {

    private final DtoMapper dtoMapper;
    private final AIService aiService;

    /**
     * Generates a GrammarLesson entity by calling the AI service and mapping the resulting DTO.
     * This service is responsible for creating the lesson entity, but not for persisting it.
     */
    public Mono<GrammarLesson> generateLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabularyLesson) {
        return aiService.generateGrammarLesson(request, vocabularyLesson)
                .map(dto -> (GrammarLesson) dtoMapper.toEntity(dto));
    }
}
