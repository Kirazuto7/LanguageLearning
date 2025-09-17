package com.example.language_learning.lessonbook.chapter.lesson.page.sentence.dtos;

import lombok.Builder;

@Builder
public record ConjugationExampleDTO(
        Long id,
        String infinitive,
        String conjugatedForm,
        String exampleSentence,
        String sentenceTranslation
) {}
