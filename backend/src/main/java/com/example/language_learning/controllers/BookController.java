package com.example.language_learning.controllers;

import com.example.language_learning.dto.models.LessonBookDTO;
import com.example.language_learning.requests.LessonBookRequest;
import com.example.language_learning.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping("/fetch/book")
    public Mono<LessonBookDTO> fetchBook(@RequestBody LessonBookRequest fetchRequest) {
        return bookService.fetchBook(fetchRequest);
    }
}
