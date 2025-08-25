package com.example.language_learning.services;

import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.entity.models.Page;
import com.example.language_learning.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChapterPersistenceService {

    private final BookService bookService;
    private final VocabularyLessonService vocabularyLessonService;
    private final GrammarLessonService grammarLessonService;
    private final ConjugationLessonService conjugationLessonService;
    private final PracticeLessonService practiceLessonService;
    private final ReadingComprehensionLessonService readingComprehensionLessonService;
    private final DtoMapper mapper;

    @Transactional
    public ChapterDTO saveChapterComponents(LessonBook book, int chapterNumber, ChapterService.GeneratedComponents components) {
        if (components == null) {
            throw new IllegalStateException("Failed to generate all required chapter components.");
        }

        Chapter chapter = createChapter(book, chapterNumber, components);
        book.addChapter(chapter);
        LessonBook savedBook = bookService.save(book);

        return savedBook.getChapters().stream()
                .filter(c -> c.getChapterNumber() == chapterNumber)
                .findFirst()
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Failed to find newly saved chapter"));
    }

    private Chapter createChapter(LessonBook book, int chapterNumber, ChapterService.GeneratedComponents components) {
        Chapter chapter = new Chapter();
        chapter.setTitle(components.metadata().title());
        chapter.setNativeTitle(components.metadata().nativeTitle());
        chapter.setChapterNumber(chapterNumber);

        int lastPageNumber = book.getChapters().stream()
                .flatMap(c -> c.getPages().stream())
                .mapToInt(Page::getPageNumber)
                .max()
                .orElse(0);

        chapter.addPage(new Page(lastPageNumber + 1, vocabularyLessonService.createVocabularyLesson(components.vocabulary())));
        if (components.grammar() != null) {
            chapter.addPage(new Page(lastPageNumber + 2, grammarLessonService.createGrammarLesson(components.grammar())));
        } else if (components.conjugation() != null) {
            chapter.addPage(new Page(lastPageNumber + 2, conjugationLessonService.createConjugationLesson(components.conjugation())));
        }
        chapter.addPage(new Page(lastPageNumber + 3, practiceLessonService.createPracticeLesson(components.practice())));
        chapter.addPage(new Page(lastPageNumber + 4, readingComprehensionLessonService.createReadingComprehensionLesson(components.reading())));

        return chapter;
    }
}