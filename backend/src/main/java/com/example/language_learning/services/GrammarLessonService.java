package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.GrammarLessonDTO;
import com.example.language_learning.entity.lessons.GrammarLesson;
import com.example.language_learning.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GrammarLessonService {

    private final DtoMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public GrammarLesson createGrammarLesson(GrammarLessonDTO dto) {
        return (GrammarLesson) mapper.toEntity(dto);
    }
}
