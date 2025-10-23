package com.example.language_learning.lessonbook.chapter.lesson.page;

import com.example.language_learning.lessonbook.chapter.lesson.dtos.LessonDTO;
import com.example.language_learning.shared.dtos.progress.ProgressData;
import lombok.Builder;


@Builder
public record LessonPageDTO(
    Long id,
    LessonDTO lesson
) implements ProgressData {}
