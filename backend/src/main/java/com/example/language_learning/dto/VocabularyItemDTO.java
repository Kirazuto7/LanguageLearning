package com.example.language_learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyItemDTO {
    private String word;
    private String romanization;
    private String translation;
}