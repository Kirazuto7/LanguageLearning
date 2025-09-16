package com.example.language_learning.ai.components;


import com.fasterxml.jackson.databind.JavaType;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A record to hold the mapping strategy for a specific AI response type.
 *
 * @param javaTypeProvider A function that provides the specific JavaType for the AI response, potentially using request parameters.
 * @param mapper A function that takes the raw AI DTO and the original request parameters, and returns the final internal DTO.
 * @param <T_AI> The raw AI DTO type.
 * @param <T_INTERNAL> The final internal DTO type.
 */
public record AIResponseMapping<T_AI, T_INTERNAL>(
        Function<Map<String, Object>, JavaType> javaTypeProvider,
        BiFunction<T_AI, Map<String, Object>, T_INTERNAL> mapper
) {}
