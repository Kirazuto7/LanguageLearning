package com.example.language_learning.services.actions;

import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.services.ChapterService;
import com.example.language_learning.services.LessonBookService;
import com.example.language_learning.services.PageService;
import com.example.language_learning.services.inputs.ChapterPrepInput;
import com.example.language_learning.services.outputs.ChapterPrepOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChapterPrepActions {
    private final LessonBookService lessonBookService;
    private final ChapterService chapterService;
    private final PageService pageService;

    public void generateTaskId(ChapterPrepInput input, ChapterPrepOutput output) {
        output.setTaskId(UUID.randomUUID().toString());
    }

    public void findOrCreateBook(ChapterPrepInput input, ChapterPrepOutput output) {
        LessonBook book = lessonBookService.findOrCreateBook(input.request().language(), input.request().difficulty(), input.user());
        output.setBook(book);
    }

    public void createInitialChapter(ChapterPrepInput input, ChapterPrepOutput output) {
        int nextChapterNumber = output.getBook().getChapters().stream()
                .mapToInt(Chapter::getChapterNumber)
                .max()
                .orElse(0) + 1;
        Chapter newChapter = chapterService.createChapter(output.getBook(), nextChapterNumber, "Generating...", "Generating...");
        output.setChapter(newChapter);
    }

    public void calculateStartingPage(ChapterPrepInput input, ChapterPrepOutput output) {
        int lastPageNumber = pageService.getLastPageNumberForBook(output.getBook().getId());
        int nextPageNumber = lastPageNumber + 1;
        output.setStartingPageNumber(nextPageNumber);
    }
}
