package com.example.language_learning.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String bookTitle;
    private String difficulty;
    private String language;
    private List<ChapterDTO> chapters;
}
