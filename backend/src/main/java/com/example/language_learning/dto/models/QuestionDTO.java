package com.example.language_learning.dto.models;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private String questionType;
    private String questionText;
    private String answer;
    private List<String> options;
}