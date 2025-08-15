package com.example.language_learning.controllers;

import com.example.language_learning.dto.models.LessonBookDTO;
import com.example.language_learning.requests.LessonBookRequest;
import com.example.language_learning.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/books")
@Slf4j
@RequiredArgsConstructor
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    @PostMapping("/fetch/book")
    public LessonBookDTO fetchBook(@Valid @RequestBody LessonBookRequest fetchRequest) {
        logger.info("Received request to generate chapter for: {}", fetchRequest);
        return bookService.fetchBook(fetchRequest);
    }
}
