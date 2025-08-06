package com.example.language_learning.dto;

import lombok.Data;

@Data
public class GenerationRequest {
    private String language;
    private String difficulty;
    private String topic;
}
