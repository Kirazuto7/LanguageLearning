package com.example.language_learning.lessonbook.chapter.dtos;

import java.util.List;

import com.example.language_learning.lessonbook.chapter.lesson.page.dtos.PageDTO;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChapterDTO (
     Long id,
     int chapterNumber,
     String title,
     String nativeTitle,
     List<PageDTO> pages
) {}