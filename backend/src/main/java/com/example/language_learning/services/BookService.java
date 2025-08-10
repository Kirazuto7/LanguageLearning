package com.example.language_learning.services;

import com.example.language_learning.dto.models.LessonBookDTO;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.BookRepository;
import com.example.language_learning.requests.LessonBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final DtoMapper dtoMapper;

    public LessonBook findOrCreateBook(String language, String difficulty) {
        return bookRepository.findByLanguageAndDifficulty(language, difficulty)
                .orElseGet(() -> {
                    LessonBook newBook = new LessonBook(null, String.format("%s for %s learners", language, difficulty), difficulty, language, new ArrayList<>());
                    return bookRepository.save(newBook);
                });
    }

    public Mono<LessonBookDTO> fetchBook(LessonBookRequest request) {
        LessonBook book = findOrCreateBook(request.getLanguage(), request.getDifficulty());
        LessonBookDTO lessonBookDTO = dtoMapper.toDto(book);
        return Mono.just(lessonBookDTO);
    }

    public LessonBook save(LessonBook book) {
        return bookRepository.save(book);
    }
}
