package com.example.language_learning.services;

import com.example.language_learning.dto.models.SentenceDTO;
import com.example.language_learning.entity.models.Sentence;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.SentenceRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SentenceService {
    private final DtoMapper mapper;
    private final SentenceRepository sentenceRepository;
    private final WordService wordService;

    @Transactional(propagation = Propagation.MANDATORY)
    public Sentence createSentence(SentenceDTO dto) {
        return mapper.toEntity(dto);
    }
}
