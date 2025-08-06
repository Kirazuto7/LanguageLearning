package com.example.language_learning.dto;

import java.util.List;

import com.example.language_learning.entity.Lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponse {
    private String title;
    private String nativeTitle;
    private List<Lesson> lessons;
}