package com.example.language_learning.dto.models;

import com.example.language_learning.dto.lessons.LessonDTO;
import com.example.language_learning.dto.progress.ProgressData;
import lombok.Builder;


@Builder
public record PageDTO (
    Long id,
    int pageNumber,
    LessonDTO lesson
) implements ProgressData {}
