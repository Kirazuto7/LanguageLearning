package com.example.language_learning.ai.components;

import com.example.language_learning.enums.PromptType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents a self-contained, immutable request for a single AI generation task.
 * This class uses a Builder pattern to ensure that all required parameters are provided
 * before the request is created.
 *
 * @param <T_AI> The type of the DTO that the raw AI JSON response will be parsed into.
 *                For example, {@code AITranslationResponse.class}.
 * @param <T_INTERNAL> The type of the final, internal application DTO after mapping.
 *                     For example, {@code TranslationResponse.class}.
 */
public final class AIRequest<T_AI, T_INTERNAL> {
    @Getter
    private final JavaType aiResponseType;
    @Getter
    private final PromptType promptType;
    @Getter
    private final Map<String, Object> params;
    @Getter
    private final Function<T_AI, T_INTERNAL> responseMapper;

    private AIRequest(Builder<T_AI, T_INTERNAL> builder) {
        this.aiResponseType = builder.aiResponseType;
        this.promptType = builder.promptType;
        this.params = builder.params;
        this.responseMapper = builder.responseMapper;
    }

    public static class Builder<T_AI, T_INTERNAL> {
        private final ObjectMapper objectMapper;
        private JavaType aiResponseType;
        private PromptType promptType;
        private String language;
        private final Map<String, Object> params = new HashMap<>();
        private final Function<T_AI, T_INTERNAL> responseMapper;

        Builder(ObjectMapper objectMapper, Function<T_AI, T_INTERNAL> responseMapper) { // Package-private constructor
            this.objectMapper = objectMapper;
            this.responseMapper = responseMapper;
        }

        Builder<T_AI, T_INTERNAL> aiResponseType(JavaType responseType) { // Package-private
            this.aiResponseType = responseType;
            return this;
        }

        Builder<T_AI, T_INTERNAL> aiResponseType(Class<T_AI> responseClass) { // Package-private
            this.aiResponseType = objectMapper.getTypeFactory().constructType(responseClass);
            return this;
        }

        public Builder<T_AI, T_INTERNAL> promptType(PromptType promptType) {
            this.promptType = promptType;
            return this;
        }

        public Builder<T_AI, T_INTERNAL> language(String language) {
            this.language = language;
            return this;
        }

        public Builder<T_AI, T_INTERNAL>  param(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public AIRequest<T_AI, T_INTERNAL>  build() {
            if (aiResponseType == null || promptType == null || language == null || language.isBlank() || responseMapper == null) {
                throw new IllegalStateException("AIResponseType, PromptType, ResponseMapper and Language must be set before building the AIRequest.");
            }

            params.put("language", language);
            
            return new AIRequest<>(this);
        }
    }
}
