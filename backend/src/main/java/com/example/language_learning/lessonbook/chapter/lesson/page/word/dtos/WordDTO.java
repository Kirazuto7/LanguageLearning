package com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos;


import lombok.Builder;


@Builder
public record WordDTO (
    Long id,
    String englishTranslation,
    String language,
    WordDetailsDTO details
) {}