package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import com.example.language_learning.entity.lessons.PracticeLesson;
import com.example.language_learning.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PracticeLessonService {

    private final DtoMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public PracticeLesson createPracticeLesson(PracticeLessonDTO dto) {
        return (PracticeLesson) mapper.toEntity(dto);
    }
}
