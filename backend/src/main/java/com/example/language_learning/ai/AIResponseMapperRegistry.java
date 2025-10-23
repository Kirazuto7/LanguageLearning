package com.example.language_learning.ai;

import com.example.language_learning.ai.components.AIResponseMapping;
import com.example.language_learning.ai.config.AIConfig;
import com.example.language_learning.ai.dtos.lessonbook.*;
import com.example.language_learning.ai.dtos.proofread.AIProofreadResponse;
import com.example.language_learning.ai.dtos.storybook.AIGeneratedStoryResponse;
import com.example.language_learning.ai.dtos.storybook.AIStoryMetadataResponse;
import com.example.language_learning.ai.dtos.translation.AITranslationResponse;
import com.example.language_learning.ai.enums.PromptType;
import com.example.language_learning.ai.mappers.AILessonMapper;
import com.example.language_learning.ai.mappers.AIResponseMapper;
import com.example.language_learning.ai.mappers.AIStoryMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A central registry for AI response mapping strategies.
 * This class implements the Registry pattern to decouple the {@link AIEngine} from specific mapping logic.
 * On startup, it registers a "recipe" (an {@link AIResponseMapping}) for each {@link PromptType}.
 * The AIEngine uses this registry to look up the correct recipe at runtime,
 * making the system easily extensible.
 */
@Component
@RequiredArgsConstructor
public class AIResponseMapperRegistry {
    private final AILessonMapper aiLessonMapper;
    private final AIResponseMapper aiResponseMapper;
    private final AIStoryMapper aiStoryMapper;
    private final ObjectMapper objectMapper;
    private final AIConfig aiConfig;

    private final Map<PromptType, AIResponseMapping<?, ?>> mappings = new HashMap<>();

    /**
     * Initializes the registry by mapping each {@link PromptType} to its corresponding response handling strategy.
     */
    @PostConstruct
    public void init() {
        registerForVocabulary();
        register(PromptType.TRANSLATE, AITranslationResponse.class, (response, params) -> aiResponseMapper.toTranslationResponse(response));
        register(PromptType.PROOFREAD, AIProofreadResponse.class, (response, params) -> aiResponseMapper.toPracticeLessonCheckResponse(response));
        register(PromptType.LESSON_METADATA, AIChapterMetadataResponse.class, (response, params) -> aiLessonMapper.toChapterMetadataDTO(response, (String) params.get("topic")));
        register(PromptType.GRAMMAR_LESSON, AIGrammarLessonResponse.class, (response, params) -> aiLessonMapper.toGrammarLessonDTO(response, (String) params.get("language")));
        register(PromptType.CONJUGATION_LESSON, AIConjugationLessonResponse.class, (response, params) -> aiLessonMapper.toConjugationLessonDTO(response, (String) params.get("language")));
        register(PromptType.PRACTICE_LESSON, AIPracticeLessonResponse.class, (response, params) -> aiLessonMapper.toPracticeLessonDTO(response, (String) params.get("language")));
        register(PromptType.READING_COMPREHENSION_LESSON, AIReadingComprehensionLessonResponse.class, (response, params) -> aiLessonMapper.toReadingComprehensionLessonDTO(response, (String) params.get("language")));
        register(PromptType.STORY_METADATA, AIStoryMetadataResponse.class, (response, params) -> aiStoryMapper.toShortStoryMetadataDTO(response, (String) params.get("genre")));
        register(PromptType.STORY_PAGES, AIGeneratedStoryResponse.class, aiStoryMapper::toShortStoryDTO);
    }

    /**
     * Helper method to register a mapping for a simple case where the AI response type is a non-generic class.
     * @param promptType The prompt type to register.
     * @param responseClass The raw AI DTO class.
     * @param mapper The function to map the AI DTO to the internal DTO.
     */
    private <T_AI, T_INTERNAL> void register(PromptType promptType, Class<T_AI> responseClass, BiFunction<T_AI, Map<String, Object>, T_INTERNAL> mapper) {
        Function<Map<String, Object>, JavaType> javaTypeProvider = params -> objectMapper.getTypeFactory().constructType(responseClass);
        mappings.put(promptType, new AIResponseMapping<>(javaTypeProvider, mapper));
    }

    /**
     * Registers the complex mapping strategy for vocabulary lessons.
     * Because the {@link AIVocabularyLessonResponse} is generic (e.g., {@code AIVocabularyLessonResponse<JapaneseWordDetailsDTO>}),
     * its full {@link JavaType} must be constructed dynamically at runtime based on the request's language parameter.
     * This method creates a {@code javaTypeProvider} function that encapsulates this dynamic type construction logic.
     */
    private void registerForVocabulary() {
        Function<Map<String, Object>, JavaType> javaTypeProvider = params -> {
            String language = (String) params.get("language");
            Class<?> itemDtoClass = aiConfig.getVocabularyItemDtoClass(language);
            return objectMapper.getTypeFactory().constructParametricType(AIVocabularyLessonResponse.class, itemDtoClass);
        };

        BiFunction<AIVocabularyLessonResponse<?>, Map<String, Object>, ?> mapper = (response, params) ->
            aiLessonMapper.toVocabularyLessonDTO(response, (String) params.get("language"));

        mappings.put(PromptType.VOCABULARY_LESSON, new AIResponseMapping<>(javaTypeProvider, mapper));
    }

    /**
     * Retrieves the mapping strategy for a given {@link PromptType}.
     * @param type The prompt type.
     * @return The corresponding {@link AIResponseMapping}.
     */
    @SuppressWarnings("unchecked")
    public <T_AI, T_INTERNAL> AIResponseMapping<T_AI, T_INTERNAL> get(PromptType type) {
        return (AIResponseMapping<T_AI, T_INTERNAL>) mappings.get(type);
    }
}
