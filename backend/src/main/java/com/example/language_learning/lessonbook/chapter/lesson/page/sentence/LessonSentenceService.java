package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import com.example.language_learning.shared.mapper.DtoMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonSentenceService {
    private final DtoMapper mapper;
    private final LessonSentenceRepository lessonSentenceRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public LessonSentence createSentence(LessonSentenceDTO dto) {
        return mapper.toEntity(dto);
    }
}
