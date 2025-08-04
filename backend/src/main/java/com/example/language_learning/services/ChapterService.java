package com.example.language_learning.services;

import com.example.language_learning.dto.ChapterResponse;
import com.example.language_learning.dto.GenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final AIService aiService;

    public Mono<ChapterResponse> generateChapter(GenerationRequest request) {
        return aiService.generateChapter(request);
    }
}
