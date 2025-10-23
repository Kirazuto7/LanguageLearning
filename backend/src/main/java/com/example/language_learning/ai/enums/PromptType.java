package com.example.language_learning.ai.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromptType {
    // Lessonbook prompts
    LESSON_METADATA("lessons/metadata", "chapter_metadata"),
    VOCABULARY_LESSON("lessons/vocabulary", "vocabulary_lesson"),
    GRAMMAR_LESSON("lessons/grammar", "grammar_lesson"),
    CONJUGATION_LESSON("lessons/conjugation", "conjugation_lesson"),
    PRACTICE_LESSON("lessons/practice", "practice_lesson"),
    READING_COMPREHENSION_LESSON("lessons/reading", "reading_comprehension_lesson"),

    // Storybook prompts
    STORY_METADATA("storybook/metadata", "story_metadata"),
    STORY_PAGES("storybook/pages", "story_pages"),

    // Shared prompts
    TRANSLATE("translation", "translation"),
    PROOFREAD("proofread", "proofread");

    private final String category;
    private final String fileName;
}
