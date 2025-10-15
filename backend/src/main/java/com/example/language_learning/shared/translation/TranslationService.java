package com.example.language_learning.shared.translation;

import com.example.language_learning.ai.AIEngine;
import com.example.language_learning.ai.components.AIRequest;
import com.example.language_learning.ai.enums.PromptType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class TranslationService {
    private final AIEngine aiEngine;

    public Mono<TranslationResponse> translateText(TranslationRequest request) {
        AIRequest<TranslationResponse> aiRequest = AIRequest.builder()
                .responseClass(TranslationResponse.class)
                .promptType(PromptType.TRANSLATE)
                .language(request.sourceLanguage())
                .param("textToTranslate", request.textToTranslate())
                .param("sourceLanguage", request.sourceLanguage())
                .withModeration(true)
                .build();
        
        return aiEngine.generate(aiRequest);
    }
}
