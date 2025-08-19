package com.example.language_learning.services;

import com.example.language_learning.dto.models.WordDTO;
import com.example.language_learning.entity.models.Word;
import com.example.language_learning.mapper.DtoMapper;
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
