package com.example.language_learning.services;

import com.example.language_learning.entity.lessons.Lesson;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.Page;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.PageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class PageService {

    private final DtoMapper dtoMapper;
    private final PageRepository pageRepository;

    /**
     * Creates a new Page entity, links it to a chapter and a lesson,
     * and persists it to the database in a new transaction.
     */
    @Transactional
    public Mono<Page> createAndPersistPage(Chapter chapter, Lesson lesson, int pageNumber) {
        return Mono.fromCallable(() -> {
           Page page = Page.builder()
                   .pageNumber(pageNumber)
                   .lesson(lesson)
                   .chapter(chapter)
                   .build();
           if (lesson != null) {
               lesson.setPage(page); 
           }
           chapter.addPage(page);
           return pageRepository.save(page);
        });
    }

    public int getLastPageNumberForBook(Long bookId) {
        return pageRepository.findMaxPageNumberByBookId(bookId).orElse(0);
    }
}
