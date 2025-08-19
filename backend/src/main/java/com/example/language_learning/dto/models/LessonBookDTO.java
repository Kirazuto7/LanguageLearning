package com.example.language_learning.dto.models;

import java.util.List;

import com.example.language_learning.dto.user.UserDTO;
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
