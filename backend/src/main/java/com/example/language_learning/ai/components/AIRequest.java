package com.example.language_learning.ai.components;

import com.example.language_learning.ai.enums.PromptType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a self-contained, immutable request for a single AI generation task.
 * This class uses a Builder pattern to ensure that all required parameters are provided
 * before the request is created.
 *
 * @param <T_INTERNAL> The type of the final, internal application DTO that this request will produce.
 *                     For example, {@code TranslationResponse.class}.
 */
public final class AIRequest<T_INTERNAL> {
    @Getter
    private final PromptType promptType;
    @Getter
    private final Map<String, Object> params;
    @Getter
    private final boolean withModeration;

    private AIRequest(Builder<T_INTERNAL> builder) {
        this.promptType = builder.promptType;
        this.params = builder.params;
        this.withModeration = builder.withModeration;
    }

    public static IResponseClassStep builder() {
        return new ResponseClassStep();
    }

    public interface IResponseClassStep {
        <T_INTERNAL> IBuilder<T_INTERNAL> responseClass(Class<T_INTERNAL> targetClass);
    }

    public interface IBuilder<T_INTERNAL> {
        IBuilder<T_INTERNAL> promptType(PromptType promptType);
        IBuilder<T_INTERNAL> language(String language);
        IBuilder<T_INTERNAL> param(String key, Object value);
        IBuilder<T_INTERNAL> withModeration(boolean with);
        AIRequest<T_INTERNAL> build();
    }

    /**
     * An intermediate step in the builder chain to specify the target DTO type.
     * This allows for a more fluent API and helps the compiler with type inference.
     */
    private static class ResponseClassStep implements IResponseClassStep {
        private ResponseClassStep() {}

        @Override
        public <T_INTERNAL> IBuilder<T_INTERNAL> responseClass(Class<T_INTERNAL> targetClass) {
            // The targetClass parameter's primary purpose is to provide a type hint to the
            // Java compiler, enabling it to infer the generic type for the builder.
            return new AIRequest.Builder<>();
        }
    }

    private static class Builder<T_INTERNAL> implements IBuilder<T_INTERNAL> {
        private PromptType promptType;
        private String language;
        private final Map<String, Object> params = new HashMap<>();
        private boolean withModeration = false; // Default to false

        private Builder() { // private constructor
        }

        @Override
        public IBuilder<T_INTERNAL> promptType(PromptType promptType) {
            this.promptType = promptType;
            return this;
        }

        @Override
        public IBuilder<T_INTERNAL> language(String language) {
            this.language = language;
            return this;
        }

        @Override
        public IBuilder<T_INTERNAL>  param(String key, Object value) {
            params.put(key, value);
            return this;
        }

        @Override
        public IBuilder<T_INTERNAL> withModeration(boolean with) {
            this.withModeration = with;
            return this;
        }

        @Override
        public AIRequest<T_INTERNAL>  build() {
            if (promptType == null || language == null || language.isBlank()) {
                throw new IllegalStateException("PromptType and Language must be set before building the AIRequest.");
            }

            params.put("language", language);
            
            return new AIRequest<>(this);
        }
    }
}
