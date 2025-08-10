package com.example.language_learning.dto.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceDTO {
    private Long id;
    private String question;
    private String answer;
    private List<String> answerChoices;
}
