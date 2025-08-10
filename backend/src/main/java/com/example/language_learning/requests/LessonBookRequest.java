package com.example.language_learning.requests;

import lombok.Data;

@Data
public class LessonBookRequest {
    private String language;
    private String difficulty;
}
