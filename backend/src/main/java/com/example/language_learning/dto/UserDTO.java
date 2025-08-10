package com.example.language_learning.dto;

import com.example.language_learning.dto.models.LessonBookDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private SettingsDTO settings;
    private List<LessonBookDTO> lessonBookList;
}
