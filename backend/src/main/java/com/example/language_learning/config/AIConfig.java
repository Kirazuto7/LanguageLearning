package com.example.language_learning.config;

import com.example.language_learning.enums.PromptType;
import io.jsonwebtoken.security.Jwks;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
public class AIConfig {
    @Builder
    private record AIAsset (String modelName, Map<PromptType, Resource> prompts) {}
    /**
     *  Row Key: Language (e.g, "japanese", "korean")
     *  Value: The Asset
     */
    private final Map<String, AIAsset> aiAssets = new HashMap<>();
    @Value("classpath:prompts/chapter_metadata_prompt.txt")
    private Resource chapterMetadataPrompt;
    @Value("classpath:prompts/vocabulary_lesson_prompt.txt")
    private Resource vocabularyLessonPrompt;
    @Value("classpath:prompts/japanese/japanese_vocabulary_lesson_prompt.txt")
    private Resource japaneseVocabularyLessonPrompt;
    @Value("classpath:prompts/grammar_lesson_prompt.txt")
    private Resource grammarLessonPrompt;
    @Value("classpath:prompts/japanese/japanese_grammar_lesson_prompt.txt")
    private Resource japaneseGrammarLessonPrompt;
    @Value("classpath:prompts/conjugation_lesson_prompt.txt")
    private Resource conjugationLessonPrompt;
    @Value("classpath:prompts/japanese/japanese_conjugation_lesson_prompt.txt")
    private Resource japaneseConjugationLessonPrompt;
    @Value("classpath:prompts/practice_lesson_prompt.txt")
    private Resource practiceLessonPrompt;
    @Value("classpath:prompts/japanese/japanese_practice_lesson_prompt.txt")
    private Resource japanesePracticeLessonPrompt;
    @Value("classpath:prompts/reading_comprehension_lesson_prompt.txt")
    private Resource readingComprehensionLessonPrompt;
    @Value("classpath:prompts/japanese/japanese_reading_comprehension_lesson_prompt.txt")
    private Resource japaneseReadingComprehensionLessonPrompt;

    @PostConstruct
    public void init() {
        Map<PromptType, Resource> defaultPrompts = new HashMap<>();
        defaultPrompts.put(PromptType.METADATA, chapterMetadataPrompt);
        defaultPrompts.put(PromptType.VOCABULARY, vocabularyLessonPrompt);
        defaultPrompts.put(PromptType.GRAMMAR, grammarLessonPrompt);
        defaultPrompts.put(PromptType.CONJUGATION, conjugationLessonPrompt);
        defaultPrompts.put(PromptType.PRACTICE, practiceLessonPrompt);
        defaultPrompts.put(PromptType.READING_COMPREHENSION, readingComprehensionLessonPrompt);
        AIAsset defaultAsset = AIAsset.builder().modelName("qwen3").prompts(defaultPrompts).build();

        AIAsset koreanAsset = AIAsset.builder().modelName("exaone").prompts(defaultPrompts).build();

        Map<PromptType, Resource> japanesePrompts = new HashMap<>(defaultPrompts);
        japanesePrompts.put(PromptType.VOCABULARY, japaneseVocabularyLessonPrompt);
        japanesePrompts.put(PromptType.GRAMMAR, japaneseGrammarLessonPrompt);
        japanesePrompts.put(PromptType.CONJUGATION, japaneseConjugationLessonPrompt);
        japanesePrompts.put(PromptType.PRACTICE, japanesePracticeLessonPrompt);
        japanesePrompts.put(PromptType.READING_COMPREHENSION, japaneseReadingComprehensionLessonPrompt);
        AIAsset japaneseAsset = AIAsset.builder().modelName("starling").prompts(japanesePrompts).build();

        aiAssets.put("default", defaultAsset);
        aiAssets.put("korean", koreanAsset);
        aiAssets.put("japanese", japaneseAsset);
    }

    public Optional<Resource> getPrompt(String language, PromptType promptType) {
        AIAsset asset = aiAssets.get(language.toLowerCase());
        if(asset != null) {
            Resource prompt = asset.prompts().get(promptType);
            if(prompt != null) {
                return Optional.of(prompt);
            }
        }
        AIAsset defaultAsset = aiAssets.get("default");
        return Optional.ofNullable(defaultAsset).map(obj -> obj.prompts().get(promptType));
    }

    public Optional<String> getModelName(String language) {
        AIAsset asset = aiAssets.get(language.toLowerCase());
        if(asset != null) {
            return Optional.of(asset.modelName());
        }
        return Optional.ofNullable(aiAssets.get("default")).map(AIAsset::modelName);
    }

}
