package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import lombok.Builder;

@Builder
public record LessonConjugationExampleDTO(
        Long id,
        String infinitive,
        String conjugatedForm,
        String exampleSentence,
        String sentenceTranslation
) {}
