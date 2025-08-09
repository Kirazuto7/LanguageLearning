package com.example.language_learning.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReadingComprehensionLessonDTO extends LessonDTO{
    private String story;
    private List<MultipleChoiceDTO> questions;
}
