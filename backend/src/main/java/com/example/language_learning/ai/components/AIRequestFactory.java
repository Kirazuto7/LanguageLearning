package com.example.language_learning.ai.components;

import com.example.language_learning.ai.dtos.AIVocabularyLessonResponse;
import com.example.language_learning.config.AIConfig;
import com.example.language_learning.exceptions.LanguageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AIRequestFactory {

    @Getter
    private final ObjectMapper objectMapper;
    private final AIConfig aiConfig;

    public <T_AI, T_INTERNAL> AIRequest.Builder<T_AI, T_INTERNAL> builder(Class<T_AI> responseClass, Function<T_AI, T_INTERNAL> responseMapper) {
        AIRequest.Builder<T_AI, T_INTERNAL> builder = new AIRequest.Builder<>(objectMapper, responseMapper);
        builder.aiResponseType(responseClass);
        return builder;
    }

    public <T_AI, T_INTERNAL> AIRequest.Builder<T_AI, T_INTERNAL> builder(JavaType responseType, Function<T_AI, T_INTERNAL> responseMapper) {
        AIRequest.Builder<T_AI, T_INTERNAL> builder = new AIRequest.Builder<>(objectMapper, responseMapper);
        builder.aiResponseType(responseType);
        return builder;
    }

    public <T_INTERNAL> AIRequest.Builder<AIVocabularyLessonResponse<?>, T_INTERNAL> vocabularyBuilder(String language, Function<AIVocabularyLessonResponse<?>, T_INTERNAL> responseMapper) {
        Class<?> itemDtoClass = aiConfig.getVocabularyItemDtoClass(language);
        if (itemDtoClass == null) {
            throw new LanguageException("Unsupported language for vocabulary generation: " + language);
        }
        JavaType responseType = objectMapper.getTypeFactory().constructParametricType(AIVocabularyLessonResponse.class, itemDtoClass);

        AIRequest.Builder<AIVocabularyLessonResponse<?>, T_INTERNAL> builder = new AIRequest.Builder<>(objectMapper, responseMapper);
        builder.aiResponseType(responseType);
        return builder;
    }
}