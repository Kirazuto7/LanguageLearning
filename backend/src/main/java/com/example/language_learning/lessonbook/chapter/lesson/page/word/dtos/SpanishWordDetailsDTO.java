package com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos;

import lombok.Builder;

@Builder
public record SpanishWordDetailsDTO(
        String lemma,
        String gender,
        String pluralForm
) implements WordDetailsDTO {}