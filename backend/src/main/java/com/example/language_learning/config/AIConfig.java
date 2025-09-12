package com.example.language_learning.config;

import com.example.language_learning.dto.api.details.*;
import com.example.language_learning.enums.PromptType;
import com.example.language_learning.exceptions.LanguageException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AIConfig {

    // --- Injected Dependencies ---
    private final ObjectMapper objectMapper;

    @Builder
    public record AIPrompt(Resource instruction, JsonNode schema) {}

    @Builder
    public record AIAsset (String modelName, Map<PromptType, AIPrompt> prompts) {}

    /**
     *  Row Key: Language (e.g, "japanese", "korean")
     *  Value: The Asset
     */
    private final Map<String, AIAsset> aiAssets = new HashMap<>();
    private final Map<String, Class<?>> vocabularyItemDtoMap = new HashMap<>();

    // This record bundles all the file-based resources for a single language configuration.
    private record LanguageAssetResources(
            String modelName,
            // Instructions
            Resource metadataInstruction,
            Resource vocabularyInstruction,
            Resource grammarInstruction,
            Resource conjugationInstruction,
            Resource practiceInstruction,
            Resource readingInstruction,
            Resource proofreadInstruction,
            // Schemas
            Resource metadataSchema,
            Resource vocabularySchema,
            Resource grammarSchema,
            Resource conjugationSchema,
            Resource practiceSchema,
            Resource readingSchema,
            Resource proofreadSchema
    ) { }

    @PostConstruct
    public void init() {
        Map<String, LanguageAssetResources> languageResourcesMap = defineLanguageResources();

        languageResourcesMap.forEach((language, resources) ->
            aiAssets.put(language, buildAsset(language, resources))
        );

        initializeVocabularyMap();
    }

    private Map<String, LanguageAssetResources> defineLanguageResources() {
        Map<String, LanguageAssetResources> map = new HashMap<>();
        // Asian Languages - Using specific vocabulary instructions but sharing others
        map.put("japanese", new LanguageAssetResources("exaone", metadataInstruction, japaneseVocabularyInstruction, grammarInstruction, conjugationInstruction, practiceInstruction, readingInstruction, proofreadInstruction, japaneseMetadataSchema, japaneseVocabularySchema, japaneseGrammarSchema, japaneseConjugationSchema, japanesePracticeSchema, japaneseReadingSchema, japaneseProofreadSchema));
        map.put("korean", new LanguageAssetResources("exaone", metadataInstruction, koreanVocabularyInstruction, grammarInstruction, conjugationInstruction, practiceInstruction, readingInstruction, proofreadInstruction, koreanMetadataSchema, koreanVocabularySchema, koreanGrammarSchema, koreanConjugationSchema, koreanPracticeSchema, koreanReadingSchema, koreanProofreadSchema));
        map.put("chinese", new LanguageAssetResources("qwen3", metadataInstruction, chineseVocabularyInstruction, grammarInstruction, conjugationInstruction, practiceInstruction, readingInstruction, proofreadInstruction, chineseMetadataSchema, chineseVocabularySchema, chineseGrammarSchema, chineseConjugationSchema, chinesePracticeSchema, chineseReadingSchema, chineseProofreadSchema));
        map.put("thai", new LanguageAssetResources("qwen3", metadataInstruction, thaiVocabularyInstruction, grammarInstruction, conjugationInstruction, practiceInstruction, readingInstruction, proofreadInstruction, thaiMetadataSchema, thaiVocabularySchema, thaiGrammarSchema, thaiConjugationSchema, thaiPracticeSchema, thaiReadingSchema, thaiProofreadSchema));
        // European Languages - Using specific vocabulary instructions but sharing others
        map.put("italian", new LanguageAssetResources("qwen3", metadataInstruction, italianVocabularyInstruction, grammarInstruction, conjugationInstruction, practiceInstruction, readingInstruction, proofreadInstruction, latinExtendedMetadataSchema, italianVocabularySchema, latinExtendedGrammarSchema, latinExtendedConjugationSchema, latinExtendedPracticeSchema, latinExtendedReadingSchema, latinExtendedProofreadSchema));
        map.put("spanish", new LanguageAssetResources("qwen3", metadataInstruction, spanishVocabularyInstruction, grammarInstruction, conjugationInstruction, practiceInstruction, readingInstruction, proofreadInstruction, latinExtendedMetadataSchema, spanishVocabularySchema, latinExtendedGrammarSchema, latinExtendedConjugationSchema, latinExtendedPracticeSchema, latinExtendedReadingSchema, latinExtendedProofreadSchema));
        map.put("french", new LanguageAssetResources("qwen3", metadataInstruction, frenchVocabularyInstruction, grammarInstruction, conjugationInstruction, practiceInstruction, readingInstruction, proofreadInstruction, latinExtendedMetadataSchema, frenchVocabularySchema, latinExtendedGrammarSchema, latinExtendedConjugationSchema, latinExtendedPracticeSchema, latinExtendedReadingSchema, latinExtendedProofreadSchema));
        map.put("german", new LanguageAssetResources("qwen3", metadataInstruction, germanVocabularyInstruction, grammarInstruction, conjugationInstruction, practiceInstruction, readingInstruction, proofreadInstruction, latinExtendedMetadataSchema, germanVocabularySchema, latinExtendedGrammarSchema, latinExtendedConjugationSchema, latinExtendedPracticeSchema, latinExtendedReadingSchema, latinExtendedProofreadSchema));
        return map;
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
        return vocabularyItemDtoMap.get(language.toLowerCase());
    }

    private AIAsset buildAsset(String language, LanguageAssetResources resources) {
        String modelName = resources.modelName();
        Map<PromptType, AIPrompt> prompts = new HashMap<>();
        prompts.put(PromptType.METADATA, AIPrompt.builder().instruction(resources.metadataInstruction()).schema(readSchemaAsJsonNode(resources.metadataSchema())).build());
        prompts.put(PromptType.VOCABULARY, AIPrompt.builder().instruction(resources.vocabularyInstruction()).schema(readSchemaAsJsonNode(resources.vocabularySchema())).build());
        prompts.put(PromptType.GRAMMAR, AIPrompt.builder().instruction(resources.grammarInstruction()).schema(readSchemaAsJsonNode(resources.grammarSchema())).build());
        prompts.put(PromptType.CONJUGATION, AIPrompt.builder().instruction(resources.conjugationInstruction()).schema(readSchemaAsJsonNode(resources.conjugationSchema())).build());
        prompts.put(PromptType.PRACTICE, AIPrompt.builder().instruction(resources.practiceInstruction()).schema(readSchemaAsJsonNode(resources.practiceSchema())).build());
        prompts.put(PromptType.READING_COMPREHENSION, AIPrompt.builder().instruction(resources.readingInstruction()).schema(readSchemaAsJsonNode(resources.readingSchema())).build());
        prompts.put(PromptType.PROOFREAD, AIPrompt.builder().instruction(resources.proofreadInstruction()).schema(readSchemaAsJsonNode(resources.proofreadSchema())).build());
        return AIAsset.builder().modelName(modelName).prompts(prompts).build();
    }

    private JsonNode readSchemaAsJsonNode(Resource resource) {
        try {
            return objectMapper.readTree(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse schema file: " + resource.getFilename(), e);
        }
    }

    // --- Configuration Resources ---
    // Instructions
    @Value("classpath:prompts/lessons/metadata/instructions/chapter_metadata_prompt.txt")
    private Resource metadataInstruction;
    @Value("classpath:prompts/lessons/grammar/instructions/grammar_lesson_prompt.txt")
    private Resource grammarInstruction;
    @Value("classpath:prompts/lessons/conjugation/instructions/conjugation_lesson_prompt.txt")
    private Resource conjugationInstruction;
    @Value("classpath:prompts/lessons/practice/instructions/practice_lesson_prompt.txt")
    private Resource practiceInstruction;
    @Value("classpath:prompts/lessons/reading/instructions/reading_comprehension_lesson_prompt.txt")
    private Resource readingInstruction;
    @Value("classpath:prompts/proofread/instructions/proofread_prompt.txt")
    private Resource proofreadInstruction;
    @Value("classpath:prompts/lessons/vocabulary/instructions/japanese_vocabulary_lesson_prompt.txt")
    private Resource japaneseVocabularyInstruction;
    @Value("classpath:prompts/lessons/vocabulary/instructions/korean_vocabulary_lesson_prompt.txt")
    private Resource koreanVocabularyInstruction;
    @Value("classpath:prompts/lessons/vocabulary/instructions/chinese_vocabulary_lesson_prompt.txt")
    private Resource chineseVocabularyInstruction;
    @Value("classpath:prompts/lessons/vocabulary/instructions/thai_vocabulary_lesson_prompt.txt")
    private Resource thaiVocabularyInstruction;
    @Value("classpath:prompts/lessons/vocabulary/instructions/italian_vocabulary_lesson_prompt.txt")
    private Resource italianVocabularyInstruction;
    @Value("classpath:prompts/lessons/vocabulary/instructions/spanish_vocabulary_lesson_prompt.txt")
    private Resource spanishVocabularyInstruction;
    @Value("classpath:prompts/lessons/vocabulary/instructions/french_vocabulary_lesson_prompt.txt")
    private Resource frenchVocabularyInstruction;
    @Value("classpath:prompts/lessons/vocabulary/instructions/german_vocabulary_lesson_prompt.txt")
    private Resource germanVocabularyInstruction;

    // Schemas
    @Value("classpath:prompts/lessons/metadata/schemas/japanese_chapter_metadata_schema.json")
    private Resource japaneseMetadataSchema;
    @Value("classpath:prompts/lessons/metadata/schemas/korean_chapter_metadata_schema.json")
    private Resource koreanMetadataSchema;
    @Value("classpath:prompts/lessons/metadata/schemas/chinese_chapter_metadata_schema.json")
    private Resource chineseMetadataSchema;
    @Value("classpath:prompts/lessons/metadata/schemas/thai_chapter_metadata_schema.json")
    private Resource thaiMetadataSchema;
    @Value("classpath:prompts/lessons/metadata/schemas/latin_extended_chapter_metadata_schema.json")
    private Resource latinExtendedMetadataSchema;
    @Value("classpath:prompts/lessons/vocabulary/schemas/japanese_vocabulary_lesson_schema.json")
    private Resource japaneseVocabularySchema;
    @Value("classpath:prompts/lessons/vocabulary/schemas/korean_vocabulary_lesson_schema.json")
    private Resource koreanVocabularySchema;
    @Value("classpath:prompts/lessons/vocabulary/schemas/chinese_vocabulary_lesson_schema.json")
    private Resource chineseVocabularySchema;
    @Value("classpath:prompts/lessons/vocabulary/schemas/thai_vocabulary_lesson_schema.json")
    private Resource thaiVocabularySchema;
    @Value("classpath:prompts/lessons/vocabulary/schemas/italian_vocabulary_lesson_schema.json")
    private Resource italianVocabularySchema;
    @Value("classpath:prompts/lessons/vocabulary/schemas/spanish_vocabulary_lesson_schema.json")
    private Resource spanishVocabularySchema;
    @Value("classpath:prompts/lessons/vocabulary/schemas/french_vocabulary_lesson_schema.json")
    private Resource frenchVocabularySchema;
    @Value("classpath:prompts/lessons/vocabulary/schemas/german_vocabulary_lesson_schema.json")
    private Resource germanVocabularySchema;
    @Value("classpath:prompts/lessons/grammar/schemas/japanese_grammar_lesson_schema.json")
    private Resource japaneseGrammarSchema;
    @Value("classpath:prompts/lessons/grammar/schemas/korean_grammar_lesson_schema.json")
    private Resource koreanGrammarSchema;
    @Value("classpath:prompts/lessons/grammar/schemas/chinese_grammar_lesson_schema.json")
    private Resource chineseGrammarSchema;
    @Value("classpath:prompts/lessons/grammar/schemas/thai_grammar_lesson_schema.json")
    private Resource thaiGrammarSchema;
    @Value("classpath:prompts/lessons/grammar/schemas/latin_extended_grammar_lesson_schema.json")
    private Resource latinExtendedGrammarSchema;
    @Value("classpath:prompts/lessons/conjugation/schemas/japanese_conjugation_lesson_schema.json")
    private Resource japaneseConjugationSchema;
    @Value("classpath:prompts/lessons/conjugation/schemas/korean_conjugation_lesson_schema.json")
    private Resource koreanConjugationSchema;
    @Value("classpath:prompts/lessons/conjugation/schemas/chinese_conjugation_lesson_schema.json")
    private Resource chineseConjugationSchema;
    @Value("classpath:prompts/lessons/conjugation/schemas/thai_conjugation_lesson_schema.json")
    private Resource thaiConjugationSchema;
    @Value("classpath:prompts/lessons/conjugation/schemas/latin_extended_conjugation_lesson_schema.json")
    private Resource latinExtendedConjugationSchema;
    @Value("classpath:prompts/lessons/practice/schemas/japanese_practice_lesson_schema.json")
    private Resource japanesePracticeSchema;
    @Value("classpath:prompts/lessons/practice/schemas/korean_practice_lesson_schema.json")
    private Resource koreanPracticeSchema;
    @Value("classpath:prompts/lessons/practice/schemas/chinese_practice_lesson_schema.json")
    private Resource chinesePracticeSchema;
    @Value("classpath:prompts/lessons/practice/schemas/thai_practice_lesson_schema.json")
    private Resource thaiPracticeSchema;
    @Value("classpath:prompts/lessons/practice/schemas/latin_extended_practice_lesson_schema.json")
    private Resource latinExtendedPracticeSchema;
    @Value("classpath:prompts/lessons/reading/schemas/japanese_reading_comprehension_lesson_schema.json")
    private Resource japaneseReadingSchema;
    @Value("classpath:prompts/lessons/reading/schemas/korean_reading_comprehension_lesson_schema.json")
    private Resource koreanReadingSchema;
    @Value("classpath:prompts/lessons/reading/schemas/chinese_reading_comprehension_lesson_schema.json")
    private Resource chineseReadingSchema;
    @Value("classpath:prompts/lessons/reading/schemas/thai_reading_comprehension_lesson_schema.json")
    private Resource thaiReadingSchema;
    @Value("classpath:prompts/lessons/reading/schemas/latin_extended_reading_comprehension_lesson_schema.json")
    private Resource latinExtendedReadingSchema;
    @Value("classpath:prompts/proofread/schemas/japanese_proofread_schema.json")
    private Resource japaneseProofreadSchema;
    @Value("classpath:prompts/proofread/schemas/korean_proofread_schema.json")
    private Resource koreanProofreadSchema;
    @Value("classpath:prompts/proofread/schemas/chinese_proofread_schema.json")
    private Resource chineseProofreadSchema;
    @Value("classpath:prompts/proofread/schemas/thai_proofread_schema.json")
    private Resource thaiProofreadSchema;
    @Value("classpath:prompts/proofread/schemas/latin_extended_proofread_schema.json")
    private Resource latinExtendedProofreadSchema;
}
