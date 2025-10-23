package com.example.language_learning.ai.config.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StableDiffusionImageResponse {
    private List<String> images;
    private Map<String, Object> parameters;
    private String info;
}
