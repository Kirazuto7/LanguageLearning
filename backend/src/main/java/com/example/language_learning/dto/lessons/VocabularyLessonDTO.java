package com.example.language_learning.dto.lessons;

import com.example.language_learning.dto.models.VocabularyWordDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VocabularyLessonDTO extends LessonDTO {
    private List<VocabularyWordDTO> vocabularies;
}
