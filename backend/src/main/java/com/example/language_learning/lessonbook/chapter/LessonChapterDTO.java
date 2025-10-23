package com.example.language_learning.lessonbook.chapter;

import java.util.List;

import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageDTO;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record LessonChapterDTO(
     Long id,
     String title,
     String nativeTitle,
     List<LessonPageDTO> lessonPages
) {}