package com.example.language_learning.lessonbook.chapter.lesson.page;

import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.lessonbook.chapter.data.Chapter;
import com.example.language_learning.lessonbook.chapter.lesson.page.data.Page;
import com.example.language_learning.lessonbook.chapter.data.ChapterRepository;
import com.example.language_learning.lessonbook.chapter.lesson.page.data.PageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final ChapterRepository chapterRepository;

    /**
     * Creates a new Page entity, links it to a chapter and a lesson,
     * and persists it to the database in a new transaction.
     */
    @Transactional
    public Page createAndPersistPage(Chapter chapter, Lesson lesson, int pageNumber) {
       Chapter managedChapter = chapterRepository.findByIdWithPages(chapter.getId())
               .orElseThrow(() -> new RuntimeException("Chapter not found during page creation: " + chapter.getId()));
       Page page = Page.builder()
               .pageNumber(pageNumber)
               .lesson(lesson)
               .chapter(managedChapter)
               .build();
       if (lesson != null) {
           lesson.setPage(page);
       }
       managedChapter.addPage(page);
       return pageRepository.save(page);
    }

    public int getLastPageNumberForBook(Long bookId) {
        return pageRepository.findMaxPageNumberByBookId(bookId).orElse(0);
    }
}
