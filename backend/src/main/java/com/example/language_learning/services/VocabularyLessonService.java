package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.entity.lessons.VocabularyLesson;
import com.example.language_learning.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VocabularyLessonService {

    private final DtoMapper mapper;
    private final WordService wordService;

    @Transactional(propagation = Propagation.MANDATORY)
    public VocabularyLesson createVocabularyLesson(VocabularyLessonDTO dto) {
        VocabularyLesson lesson = (VocabularyLesson) mapper.toEntity(dto);
        dto.getVocabularies().forEach(vwDto -> {
            lesson.addWord(wordService.createWord(vwDto.getWord()), vwDto.getWordIndex());
        });
        return lesson;
    }
}
