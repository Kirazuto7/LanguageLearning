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

    @Transactional(propagation = Propagation.MANDATORY)
    public VocabularyLesson createVocabularyLesson(VocabularyLessonDTO dto) {
        return (VocabularyLesson) mapper.toEntity(dto);
    }
}
