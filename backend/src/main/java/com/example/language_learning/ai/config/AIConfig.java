package com.example.language_learning.ai.config;

import com.example.language_learning.ai.config.model.AIAsset;
import com.example.language_learning.ai.config.model.AIPrompt;
import com.example.language_learning.ai.dtos.details.*;
import com.example.language_learning.ai.enums.*;
import com.example.language_learning.shared.exceptions.LanguageException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AIConfig {
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    /**
     *  Row Key: Language (e.g, "japanese", "korean")
     *  Value: The Asset
     */
    private final Map<String, AIAsset> aiAssets = new HashMap<>();
    private final Map<String, Class<?>> vocabularyItemDtoMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Language lang : Language.values()) {
            Map<PromptType, AIPrompt> prompts = new EnumMap<>(PromptType.class);
            for (PromptType type : PromptType.values()) {
                Resource instruction = getInstructionResource(lang, type);
                Resource schema = getSchemaResource(lang, type);
                prompts.put(type, AIPrompt.builder()
                        .instruction(instruction)
                        .schema(readSchemaAsJsonNode(schema))
                        .build());
            }
            AIAsset asset = AIAsset.builder()
                    .modelName(lang.getModelName())
                    .prompts(prompts)
                    .build();
            aiAssets.put(lang.getValue(), asset);
        }

        initializeVocabularyMap();
    }

    private Resource getInstructionResource(Language lang, PromptType type) {
        String path;
        switch (type) {
            case VOCABULARY_LESSON:
                // Vocabulary instructions are unique per language
                path = String.format("classpath:prompts/%s/instructions/%s_%s_prompt.txt",
                        type.getCategory(), lang.getInstructionGroup().getPathValue(), type.getFileName());
                break;
            default:
                // Generic instructions shared by all languages for a given prompt type
                path = String.format("classpath:prompts/%s/instructions/%s_prompt.txt",
                        type.getCategory(), type.getFileName());
                break;
        }
        return resourceLoader.getResource(path);
    }

    private Resource getSchemaResource(Language lang, PromptType type) {
        final String schemaGroupPath;
        switch (type) {
            case TRANSLATE:
                // These types use a single, global schema with no prefix.
                schemaGroupPath = "";
                break;
            case VOCABULARY_LESSON:
                // Vocabulary schemas are unique to each language.
                schemaGroupPath = lang.getVocabularySchema().getPathValue() + "_";
                break;
            default:
                // Other lesson types use grouped schemas (e.g., latin_extended).
                schemaGroupPath = lang.getSchemaGroup().getPathValue() + "_";
                break;
        }

        String path = String.format("classpath:prompts/%s/schemas/%s%s_schema.json",
                type.getCategory(), schemaGroupPath, type.getFileName());
        return resourceLoader.getResource(path);
    }

    private void initializeVocabularyMap() {
        vocabularyItemDtoMap.put("japanese", AIJapaneseVocabularyItemDTO.class);
        vocabularyItemDtoMap.put("korean", AIKoreanVocabularyItemDTO.class);
        vocabularyItemDtoMap.put("chinese", AIChineseVocabularyItemDTO.class);
        vocabularyItemDtoMap.put("thai", AIThaiVocabularyItemDTO.class);
        vocabularyItemDtoMap.put("italian", AIItalianVocabularyItemDTO.class);
        vocabularyItemDtoMap.put("spanish", AISpanishVocabularyItemDTO.class);
        vocabularyItemDtoMap.put("french", AIFrenchVocabularyItemDTO.class);
        vocabularyItemDtoMap.put("german", AIGermanVocabularyItemDTO.class);
    }

    public AIPrompt getPrompt(String language, PromptType promptType) {
        AIAsset asset = aiAssets.get(language.toLowerCase());
        if (asset == null) {
            throw new LanguageException("Language '" + language + "' is not supported or configured.", null);
        }
        AIPrompt prompt = asset.prompts().get(promptType);
        if (prompt == null) {
            // This indicates a configuration error for a supported language.
            throw new IllegalStateException(
                    String.format("Prompt of type '%s' not configured for language: %s", promptType, language)
            );
        }
        return prompt;
    }

    public String getModelName(String language) {
        AIAsset asset = aiAssets.get(language.toLowerCase());
        if (asset == null) {
            throw new LanguageException("Language '" + language + "' is not supported or configured.", null);
        }
        return asset.modelName();
    }

    public Class<?> getVocabularyItemDtoClass(String language) {
        Class<?> dtoClass = vocabularyItemDtoMap.get(language.toLowerCase());
        if (dtoClass == null) {
            throw new LanguageException("Unsupported language for vocabulary generation: " + language, null);
        }
        return dtoClass;
    }

    private JsonNode readSchemaAsJsonNode(Resource resource) {
        try {
            return objectMapper.readTree(resource.getInputStream());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to parse schema file: " + resource.getFilename(), e);
        }
    }
}
