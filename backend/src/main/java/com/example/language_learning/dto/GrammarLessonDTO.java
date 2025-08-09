package com.example.language_learning.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GrammarLessonDTO extends LessonDTO {
    private String grammarConcept;
    private String explanation;
    private List<SentenceDTO> exampleSentences;
}
