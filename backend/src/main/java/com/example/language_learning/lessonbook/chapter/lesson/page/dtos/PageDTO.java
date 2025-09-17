package com.example.language_learning.lessonbook.chapter.lesson.page.dtos;

import com.example.language_learning.lessonbook.chapter.lesson.dtos.LessonDTO;
import com.example.language_learning.shared.dtos.progress.ProgressData;
import lombok.Builder;


@Builder
public record PageDTO (
    Long id,
    int pageNumber,
    LessonDTO lesson
) implements ProgressData {}
