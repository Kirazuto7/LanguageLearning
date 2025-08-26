package com.example.language_learning.dto.models;

import lombok.Builder;
import java.util.List;

@Builder
public record QuestionDTO (
    Long id,
    String questionType,
    String questionText,
    String answer,
    List<String> answerChoices
) {}