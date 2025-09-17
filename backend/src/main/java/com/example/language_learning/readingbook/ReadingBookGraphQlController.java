package com.example.language_learning.readingbook;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ReadingBookGraphQlController {
    private final ReadingBookService readingBookService;
}
