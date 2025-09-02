package com.example.language_learning.services;

import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.entity.lessons.VocabularyLesson;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VocabularyLessonService {

    private final DtoMapper dtoMapper;
    private final AIService aiService;

    /**
     * Generates a VocabularyLesson entity by calling the AI service and mapping the resulting DTO.
     * This service is responsible for creating the lesson entity, but not for persisting it.
     */
    public Mono<VocabularyLesson> generateLesson(ChapterGenerationRequest request, ChapterMetadataDTO metadata) {
        return aiService.generateVocabularyLesson(request, metadata)
                .map(dto -> (VocabularyLesson) dtoMapper.toEntity(dto));
    }
}
