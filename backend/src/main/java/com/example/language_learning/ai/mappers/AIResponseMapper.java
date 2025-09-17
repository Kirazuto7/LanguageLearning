package com.example.language_learning.ai.mappers;

import com.example.language_learning.ai.dtos.AIProofreadResponse;
import com.example.language_learning.ai.dtos.AITranslationResponse;
import com.example.language_learning.shared.responses.PracticeLessonCheckResponse;
import com.example.language_learning.shared.responses.TranslationResponse;
import org.springframework.stereotype.Component;

@Component
public class AIResponseMapper {
    public PracticeLessonCheckResponse toPracticeLessonCheckResponse(AIProofreadResponse response) {
        return PracticeLessonCheckResponse.builder()
                .isCorrect(response.correctedSentence() == null)
                .correctedSentence(response.correctedSentence())
                .feedback(response.feedback())
                .build();
    }

    public TranslationResponse toTranslationResponse(AITranslationResponse response) {
        return TranslationResponse.builder()
                .translatedText(response.translatedText())
                .build();
    }
}
