package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.LessonDTO;
import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.entity.lessons.ReadingComprehensionLesson;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.requests.ChapterGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReadingComprehensionLessonService {

    private final DtoMapper dtoMapper;
    private final AIService aiService;

    public Mono<ReadingComprehensionLesson> generateLesson(ChapterGenerationRequest request, VocabularyLessonDTO vocabularyLesson, LessonDTO specificLesson) {
        return aiService.generateReadingComprehensionLesson(request, vocabularyLesson, specificLesson)
                .map(dto -> (ReadingComprehensionLesson) dtoMapper.toEntity(dto));
    }
}
