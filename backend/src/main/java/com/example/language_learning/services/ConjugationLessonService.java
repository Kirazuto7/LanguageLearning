package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.ConjugationLessonDTO;
import com.example.language_learning.entity.lessons.ConjugationLesson;
import com.example.language_learning.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConjugationLessonService {
    private final DtoMapper dtoMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public ConjugationLesson createConjugationLesson(ConjugationLessonDTO dto) {
        return (ConjugationLesson) dtoMapper.toEntity(dto);
    }
}
