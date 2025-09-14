package com.example.language_learning.services;

import com.example.language_learning.requests.TranslationRequest;
import com.example.language_learning.responses.TranslationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class TranslationService {
    private final AIService aiService;

    public Mono<TranslationResponse> translateText(TranslationRequest request) {
        return aiService.translate(request.textToTranslate(), request.sourceLanguage());
    }
}
