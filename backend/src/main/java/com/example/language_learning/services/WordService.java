package com.example.language_learning.services;

import com.example.language_learning.dto.languages.WordDTO;
import com.example.language_learning.entity.languages.JapaneseWord;
import com.example.language_learning.entity.languages.KoreanWord;
import com.example.language_learning.entity.languages.Word;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.JapaneseWordRepository;
import com.example.language_learning.repositories.KoreanWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordService {
    private final KoreanWordRepository koreanWordRepository;
    private final JapaneseWordRepository japaneseWordRepository;
    private final DtoMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public Word createWord(WordDTO wordDto) {
        Word newWord = mapper.toEntity(wordDto);

        if (newWord instanceof KoreanWord) {
            return koreanWordRepository.save((KoreanWord) newWord);
        }
        else if (newWord instanceof JapaneseWord) {
            return japaneseWordRepository.save((JapaneseWord) newWord);
        }

        throw new IllegalArgumentException("Unsupported Word type for persistence: " + newWord.getClass().getName());
    }
}
