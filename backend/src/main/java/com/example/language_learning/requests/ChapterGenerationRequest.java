package com.example.language_learning.requests;

import lombok.Data;

@Data
public class ChapterGenerationRequest {
    private String language;
    private String difficulty;
    private String topic;
}
