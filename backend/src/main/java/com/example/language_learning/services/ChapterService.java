package com.example.language_learning.services;

import com.example.language_learning.dto.lessons.GrammarLessonDTO;
import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import com.example.language_learning.dto.lessons.ReadingComprehensionLessonDTO;
import com.example.language_learning.dto.lessons.VocabularyLessonDTO;
import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.dto.models.ChapterMetadataDTO;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.Page;
import com.example.language_learning.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final BookService bookService;
    private final AIService aiService;
    private final DtoMapper mapper;

    @Transactional
    public Mono<ChapterDTO> generateNewChapter(ChapterGenerationRequest request) {
        // 1. Find an existing or create the lesson book
        LessonBook book = bookService.findOrCreateBook(request.getLanguage(), request.getDifficulty(), request.getUserId());
        int nextChapterNumber = book.getChapters().size() + 1;

        // 2. Concurrently generate all lesson components from the AI
        Mono<ChapterMetadataDTO> metadataMono = aiService.generateChapterMetadata(request);
        Mono<VocabularyLessonDTO> vocabularyMono = aiService.generateVocabularyLesson(request);
        Mono<GrammarLessonDTO> grammarMono = aiService.generateGrammarLesson(request);
        Mono<PracticeLessonDTO> practiceMono = aiService.generatePracticeLesson(request);
        Mono<ReadingComprehensionLessonDTO> readingMono = aiService.generateReadingComprehensionLesson(request);

        // 3. Assemble the chapter
        return Mono.zip(metadataMono, vocabularyMono, grammarMono, practiceMono, readingMono)
                .map(tuple -> {
                    ChapterMetadataDTO metadata = tuple.getT1();
                    VocabularyLessonDTO vocabulary = tuple.getT2();
                    GrammarLessonDTO grammar = tuple.getT3();
                    PracticeLessonDTO practice = tuple.getT4();
                    ReadingComprehensionLessonDTO reading = tuple.getT5();

                    Chapter chapter = new Chapter();
                    chapter.setTitle(metadata.getTitle());
                    chapter.setNativeTitle(metadata.getNativeTitle());
                    chapter.setChapterNumber(nextChapterNumber);

                    int lastPageNumber = book.getChapters().stream()
                            .flatMap(c -> c.getPages().stream())
                            .mapToInt(Page::getPageNumber)
                            .max()
                            .orElse(0);

                    chapter.addPage(new Page(lastPageNumber + 1, mapper.toEntity(vocabulary)));
                    chapter.addPage(new Page(lastPageNumber + 2, mapper.toEntity(grammar)));
                    chapter.addPage(new Page(lastPageNumber + 3, mapper.toEntity(practice)));
                    chapter.addPage(new Page(lastPageNumber + 4, mapper.toEntity(reading)));

                    book.addChapter(chapter);
                    LessonBook savedBook = bookService.save(book);
                    Chapter savedChapter = savedBook.getChapters().getLast();
                    return mapper.toDto(savedChapter);
                });

    }

}
