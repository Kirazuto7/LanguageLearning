package com.example.language_learning.lessonbook.chapter.lesson.page.word;

import com.example.language_learning.lessonbook.chapter.lesson.page.word.dtos.WordDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.word.data.Word;
import com.example.language_learning.mappers.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordService {
    private final DtoMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public Word createWord(WordDTO wordDto) {
        return mapper.toEntity(wordDto);
    }
}
