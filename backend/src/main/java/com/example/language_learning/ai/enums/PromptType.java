package com.example.language_learning.ai.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromptType {
    METADATA("lessons/metadata", "chapter_metadata"),
    VOCABULARY("lessons/vocabulary", "vocabulary_lesson"),
    GRAMMAR("lessons/grammar", "grammar_lesson"),
    CONJUGATION("lessons/conjugation", "conjugation_lesson"),
    PRACTICE("lessons/practice", "practice_lesson"),
    READING_COMPREHENSION("lessons/reading", "reading_comprehension_lesson"),
    TRANSLATE("translation", "translation"),
    PROOFREAD("proofread", "proofread");

    private final String category;
    private final String fileName;
}
