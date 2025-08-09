package com.example.language_learning.services;

import com.example.language_learning.dto.ChapterDTO;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.entity.Book;
import com.example.language_learning.entity.Chapter;
import com.example.language_learning.entity.Page;
import com.example.language_learning.repositories.BookRepository;
import com.example.language_learning.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final BookService bookService;
    private final AIService aiService;
    private final DtoMapper mapper;

    @Transactional
    public Mono<ChapterDTO> generateChapter(ChapterGenerationRequest request) {
        return Mono.fromCallable(() -> bookService.findOrCreateBook(request.getLanguage(), request.getDifficulty()))
            .flatMap(book -> {
                int nextChapterNumber = book.getChapters().size() + 1;
                int lastPageNumber = book.getChapters().stream()
                        .flatMap(c -> c.getPages().stream())
                        .mapToInt(Page::getPageNumber)
                        .max()
                        .orElse(0);
                System.out.println("Next chapter number: " + nextChapterNumber);
                System.out.println("Last Page Number: " + lastPageNumber);
                return aiService.generateChapter(request)
                        .map(chapterDto -> {
                            Chapter newChapter = mapper.toEntity(chapterDto);
                            newChapter.setChapterNumber(nextChapterNumber);

                            // Re-number pages to be continuous throughout the book
                            for (Page page : newChapter.getPages()) {
                                page.setPageNumber(lastPageNumber + page.getPageNumber());
                            }
                            System.out.println(newChapter);
                            book.getChapters().add(newChapter);
                            Book savedBook = bookService.save(book);

                            // Return the DTO of the newly saved chapter
                            return mapper.toDto(savedBook.getChapters().get(savedBook.getChapters().size() - 1));
                        });
            });
    }


}
