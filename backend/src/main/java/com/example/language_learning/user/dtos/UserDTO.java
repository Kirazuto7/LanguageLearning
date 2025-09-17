package com.example.language_learning.user.dtos;

import com.example.language_learning.lessonbook.dtos.LessonBookDTO;
import lombok.Builder;


import java.util.List;

@Builder
public record UserDTO (
    Long id,
    String username,
    SettingsDTO settings,
    List<LessonBookDTO> lessonBookList
) {}
