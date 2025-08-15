package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.ReadingComprehensionLessonDTO;
import com.example.language_learning.entity.lessons.ReadingComprehensionLesson;
import com.example.language_learning.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadingComprehensionLessonService {

    private final DtoMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public ReadingComprehensionLesson createReadingComprehensionLesson(ReadingComprehensionLessonDTO dto) {
        ReadingComprehensionLesson lesson = (ReadingComprehensionLesson) mapper.toEntity(dto);
        if(dto.getQuestions() != null) {
            dto.getQuestions().stream()
                    .map(mapper::toEntity)
                    .forEach(lesson::addQuestion);
        }
        return lesson;
    }
}
