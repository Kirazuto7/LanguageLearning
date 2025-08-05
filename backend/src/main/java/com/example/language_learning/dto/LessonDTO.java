package com.example.language_learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private String type;
    private String title;
    private List<VocabularyItem> items;
}