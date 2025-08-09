package com.example.language_learning.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SentenceLessonDTO extends LessonDTO {
    private List<ExampleSentenceDTO> exampleSentences;
    private List<SentenceDTO> sentences;
}
