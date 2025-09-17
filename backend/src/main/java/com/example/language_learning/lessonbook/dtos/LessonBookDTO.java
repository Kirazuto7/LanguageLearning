package com.example.language_learning.lessonbook.dtos;

import java.util.List;

import com.example.language_learning.user.dtos.UserDTO;
import com.example.language_learning.lessonbook.chapter.dtos.ChapterDTO;
import lombok.Builder;

@Builder
public record LessonBookDTO (
    Long id,
    String bookTitle,
    String difficulty,
    String language,
    List<ChapterDTO> chapters,
    UserDTO user
) {}
