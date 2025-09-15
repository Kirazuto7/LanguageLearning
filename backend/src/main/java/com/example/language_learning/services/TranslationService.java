package com.example.language_learning.services;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.ai.components.AIRequestFactory;
import com.example.language_learning.ai.dtos.AITranslationResponse;
import com.example.language_learning.enums.PromptType;
import com.example.language_learning.mapper.AIDtoMapper;
import com.example.language_learning.requests.TranslationRequest;
import com.example.language_learning.responses.TranslationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class TranslationService {
    private final AIEngine aiEngine;
    private final AIRequestFactory aiRequestFactory;
    private final AIDtoMapper AIDtoMapper;


    public Mono<TranslationResponse> translateText(TranslationRequest request) {
        AIRequest<AITranslationResponse, TranslationResponse> aiRequest = aiRequestFactory
                .builder(AITranslationResponse.class, AIDtoMapper::toTranslationResponse)
                .promptType(PromptType.TRANSLATE)
                .language(request.sourceLanguage())
                .param("textToTranslate", request.textToTranslate())
                .param("sourceLanguage", request.sourceLanguage())
                .build();

        return aiEngine.generate(aiRequest);
    }
}
