package com.example.language_learning.user;

import com.example.language_learning.lessonbook.LessonBookDTO;
import lombok.Builder;


import java.util.List;

@Builder
public record UserDTO (
    Long id,
    String username,
    SettingsDTO settings,
    List<LessonBookDTO> lessonBookList
) {}
