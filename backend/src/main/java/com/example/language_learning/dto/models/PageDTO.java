package com.example.language_learning.dto.models;

import com.example.language_learning.dto.lessons.LessonDTO;
import lombok.Builder;


@Builder
public record PageDTO (
    Long id,
    int pageNumber,
    LessonDTO lesson
) {}
