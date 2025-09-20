package com.example.language_learning.ai.actions;

import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.LessonBook;
import com.example.language_learning.lessonbook.chapter.LessonChapterService;
import com.example.language_learning.lessonbook.LessonBookService;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageService;
import com.example.language_learning.ai.inputs.ChapterPrepInput;
import com.example.language_learning.ai.outputs.ChapterPrepOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChapterPrepActions {
    private final LessonBookService lessonBookService;
    private final LessonChapterService lessonChapterService;
    private final LessonPageService lessonPageService;

    public void generateTaskId(ChapterPrepInput input, ChapterPrepOutput output) {
        output.setTaskId(UUID.randomUUID().toString());
    }

    public void findOrCreateBook(ChapterPrepInput input, ChapterPrepOutput output) {
        LessonBook book = lessonBookService.findOrCreateBook(input.request().language(), input.request().difficulty(), input.user());
        output.setBook(book);
    }

    public void createInitialChapter(ChapterPrepInput input, ChapterPrepOutput output) {
        int nextChapterNumber = output.getBook().getLessonChapters().stream()
                .mapToInt(LessonChapter::getChapterNumber)
                .max()
                .orElse(0) + 1;
        LessonChapter newLessonChapter = lessonChapterService.createChapter(output.getBook(), nextChapterNumber, "Generating...", "Generating...");
        output.setLessonChapter(newLessonChapter);
    }

    public void calculateStartingPage(ChapterPrepInput input, ChapterPrepOutput output) {
        int lastPageNumber = lessonPageService.getLastPageNumberForBook(output.getBook().getId());
        int nextPageNumber = lastPageNumber + 1;
        output.setStartingPageNumber(nextPageNumber);
    }
}
