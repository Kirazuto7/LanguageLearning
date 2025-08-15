package com.example.language_learning.dto.models;

import com.example.language_learning.dto.languages.WordDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentenceWordDTO {
    private Long id;
    private WordDTO word;
    private int wordIndex;
}
