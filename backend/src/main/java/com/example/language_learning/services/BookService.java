package com.example.language_learning.services;

import com.example.language_learning.dto.BookDTO;
import com.example.language_learning.entity.Book;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.BookRepository;
import com.example.language_learning.requests.BookRequest;
import com.example.language_learning.requests.ChapterGenerationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final DtoMapper dtoMapper;

    public Book findOrCreateBook(String language, String difficulty) {
        return bookRepository.findByLanguageAndDifficulty(language, difficulty)
                .orElseGet(() -> {
                    Book newBook = new Book(null, String.format("%s for %s learners", language, difficulty), difficulty, language, new ArrayList<>());
                    return bookRepository.save(newBook);
                });
    }

    public Mono<BookDTO> fetchBook(BookRequest request) {
        Book book = findOrCreateBook(request.getLanguage(), request.getDifficulty());
        BookDTO bookDTO = dtoMapper.toDto(book);
        return Mono.just(bookDTO);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }
}
