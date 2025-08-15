package com.example.language_learning.dto.models;

import com.example.language_learning.dto.lessons.LessonDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private Long id;
    private int pageNumber;
    private LessonDTO lesson;
}
