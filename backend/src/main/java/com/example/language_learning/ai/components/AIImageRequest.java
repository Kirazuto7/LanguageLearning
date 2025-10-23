package com.example.language_learning.ai.components;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a self-contained, immutable request for an AI image generation task.
 * This class uses a Builder pattern to construct the request.
 *
 * @param <T_INTERNAL> The type of the final, internal application DTO that this request will produce.
 */
@Getter
public final class AIImageRequest<T_INTERNAL> {
    private final Map<String, Object> params;

    private AIImageRequest(Builder<T_INTERNAL> builder) {
        this.params = builder.params;
    }

    public static IResponseClassStep builder() {
        return new ResponseClassStep();
    }

    public interface IResponseClassStep {
        <T_INTERNAL> IBuilder<T_INTERNAL> responseClass(Class<T_INTERNAL> targetClass);
    }

    public interface IBuilder<T_INTERNAL> {
        IBuilder<T_INTERNAL> param(String key, Object value);
        AIImageRequest<T_INTERNAL> build();
    }

    private static class ResponseClassStep implements IResponseClassStep {
        private ResponseClassStep() {}

        @Override
        public <T_INTERNAL> IBuilder<T_INTERNAL> responseClass(Class<T_INTERNAL> targetClass) {
            return new AIImageRequest.Builder<>();
        }
    }

    private static class Builder<T_INTERNAL> implements IBuilder<T_INTERNAL> {
        private final Map<String, Object> params = new HashMap<>();

        private Builder() {}

        @Override
        public IBuilder<T_INTERNAL> param(String key, Object value) {
            params.put(key, value);
            return this;
        }

        @Override
        public AIImageRequest<T_INTERNAL> build() {
            return new AIImageRequest<>(this);
        }
    }
}
