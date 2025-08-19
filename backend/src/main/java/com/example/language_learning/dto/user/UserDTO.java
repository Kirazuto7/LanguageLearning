package com.example.language_learning.dto.user;

import com.example.language_learning.dto.models.LessonBookDTO;
import lombok.Builder;


import java.util.List;

@Builder
public record UserDTO (
    Long id,
    String username,
    SettingsDTO settings,
    List<LessonBookDTO> lessonBookList
) {}
