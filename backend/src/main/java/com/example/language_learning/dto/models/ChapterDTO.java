package com.example.language_learning.dto.models;

import java.util.List;

import com.example.language_learning.dto.lessons.LessonDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChapterDTO {
    private Long id;
    private int chapterNumber;
    private String title;
    private String nativeTitle;
    private List<PageDTO> pages;
}