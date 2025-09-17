package com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos;

import lombok.Builder;

@Builder
public record ItalianWordDetailsDTO(
        String lemma,
        String gender,
        String pluralForm
) implements WordDetailsDTO {}