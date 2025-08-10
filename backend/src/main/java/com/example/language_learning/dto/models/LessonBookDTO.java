package com.example.language_learning.dto.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonBookDTO {
    private Long id;
    private String bookTitle;
    private String difficulty;
    private String language;
    private List<ChapterDTO> chapters;
}
