package com.example.language_learning.dto.models;

import com.example.language_learning.dto.languages.WordDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentenceDTO {
    private Long id;
    private List<WordDTO> words;
    private String text;
    private String translation;
}
