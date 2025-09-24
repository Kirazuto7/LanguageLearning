package com.example.language_learning.ai.actions;

import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.LessonBook;
import com.example.language_learning.lessonbook.chapter.LessonChapterService;
import com.example.language_learning.lessonbook.LessonBookService;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageService;
import com.example.language_learning.ai.inputs.ChapterPrepInput;
import com.example.language_learning.ai.outputs.ChapterPrepOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChapterPrepActions {
    private final LessonBookService lessonBookService;
    private final LessonChapterService lessonChapterService;
    private final LessonPageService lessonPageService;

    public void generateTaskId(ChapterPrepInput input, ChapterPrepOutput output) {
        String taskId = UUID.randomUUID().toString();
        log.debug("Generated new task ID: {}", taskId);
        output.setTaskId(taskId);
    }

    public void findOrCreateBook(ChapterPrepInput input, ChapterPrepOutput output) {
        log.debug("Finding or creating book for language: {}, difficulty: {}", input.request().language(), input.request().difficulty());
        LessonBook book = lessonBookService.findOrCreateBook(input.request().language(), input.request().difficulty(), input.user());
        log.debug("Book found/created with ID: {}", book.getId());
        output.setBook(book);
    }

    public void createInitialChapter(ChapterPrepInput input, ChapterPrepOutput output) {
        log.debug("Creating initial lessonChapter for book ID: {}", output.getBook().getId());
        int nextChapterNumber = output.getBook().getLessonChapters().stream()
                .mapToInt(LessonChapter::getChapterNumber)
                .max()
                .orElse(0) + 1;
        log.debug("Calculated next lessonChapter number: {}", nextChapterNumber);
        LessonChapter newLessonChapter = lessonChapterService.createChapter(output.getBook(), nextChapterNumber, "Generating...", "Generating...");
        log.debug("Created new LessonChapter shell. Is it null? {}. ID: {}", newLessonChapter == null, newLessonChapter != null ? newLessonChapter.getId() : "N/A");
        output.setLessonChapter(newLessonChapter);
    }

    public void calculateStartingPage(ChapterPrepInput input, ChapterPrepOutput output) {
        log.debug("Calculating starting page for book ID: {}", output.getBook().getId());
        int lastPageNumber = lessonPageService.getLastPageNumberForBook(output.getBook().getId());
        int nextPageNumber = lastPageNumber + 1;
        log.debug("Calculated next page number: {}", nextPageNumber);
        output.setStartingPageNumber(nextPageNumber);
    }
}
