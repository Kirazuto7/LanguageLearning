package com.example.language_learning.controllers;

import com.example.language_learning.dto.BookDTO;
import com.example.language_learning.requests.BookRequest;
import com.example.language_learning.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping("/fetch")
    public Mono<BookDTO> fetchBook(@RequestBody BookRequest fetchRequest) {
        return bookService.fetchBook(fetchRequest);
    }
}
