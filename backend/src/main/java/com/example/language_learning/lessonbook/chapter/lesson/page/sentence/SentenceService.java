package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.dtos.SentenceDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.data.Sentence;
import com.example.language_learning.mappers.DtoMapper;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.data.SentenceRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SentenceService {
    private final DtoMapper mapper;
    private final SentenceRepository sentenceRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Sentence createSentence(SentenceDTO dto) {
        return mapper.toEntity(dto);
    }
}
