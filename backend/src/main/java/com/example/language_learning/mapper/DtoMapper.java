package com.example.language_learning.mapper;

import com.example.language_learning.dto.*;
import com.example.language_learning.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    /* ***************** */
    /* ** Book Mapper ** */
    /* ***************** */

    public Book toEntity(BookDTO dto) {  
        Book book = new Book();
        book.setId(dto.getId());
        book.setBookTitle(dto.getBookTitle());
        book.setDifficulty(dto.getDifficulty());
        book.setLanguage(dto.getLanguage());
        if (dto.getChapters() != null) {
            book.setChapters(dto.getChapters().stream().map(this::toEntity).collect(Collectors.toList()));
        }
        return book;
    }

    public BookDTO toDto(Book entity) {
        BookDTO dto = new BookDTO();
        dto.setId(entity.getId());
        dto.setBookTitle(entity.getBookTitle());
        dto.setDifficulty(entity.getDifficulty());
        dto.setLanguage(entity.getLanguage());
        if (entity.getChapters() != null) {
            dto.setChapters(entity.getChapters().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }


    /* ******************** */
    /* ** Chapter Mapper ** */
    /* ******************** */

    public Chapter toEntity(ChapterDTO dto) {
        Chapter chapter = new Chapter();
        chapter.setId(dto.getId());
        chapter.setChapterNumber(dto.getChapterNumber());
        chapter.setTitle(dto.getTitle());
        chapter.setNativeTitle(dto.getNativeTitle());
        if (dto.getPages() != null) {
            chapter.setPages(dto.getPages().stream().map(this::toEntity).collect(Collectors.toList()));
        }
        return chapter;
    }

    public ChapterDTO toDto(Chapter entity) {
        ChapterDTO dto = new ChapterDTO();
        dto.setId(entity.getId());
        dto.setChapterNumber(entity.getChapterNumber());
        dto.setTitle(entity.getTitle());
        dto.setNativeTitle(entity.getNativeTitle());
        if (entity.getPages() != null) {
            dto.setPages(entity.getPages().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    /* ***************** */
    /* ** Page Mapper ** */
    /* ***************** */

    public Page toEntity(PageDTO dto) {
        Page page = new Page();
        page.setId(dto.getId());
        page.setPageNumber(dto.getPageNumber());
        if (dto.getLesson() != null) {
            page.setLesson(toEntity(dto.getLesson()));
        }
        return page; 
    }

    public PageDTO toDto(Page entity) {
        PageDTO dto = new PageDTO();
        dto.setId(entity.getId());
        dto.setPageNumber(entity.getPageNumber());
        if (entity.getLesson() != null) {
            dto.setLesson(toDto(entity.getLesson()));
        }
        return dto;
    }

    /* ******************* */
    /* ** Lesson Mapper ** */
    /* ******************* */

    public Lesson toEntity(LessonDTO dto) {
        if (dto instanceof VocabularyLessonDTO vocabDto) {
            VocabularyLesson lesson = new VocabularyLesson();
            lesson.setId(vocabDto.getId());
            lesson.setTitle(vocabDto.getTitle());
            lesson.setType(vocabDto.getType());
            if (vocabDto.getItems() != null) {
                lesson.setVocabularyItems(vocabDto.getItems().stream().map(this::toEntity).collect(Collectors.toList()));
            }
            return lesson;
        }
        else if(dto instanceof SentenceLessonDTO sentenceLessonDTO) {
            SentenceLesson lesson = new SentenceLesson();
            lesson.setId(sentenceLessonDTO.getId());
            lesson.setTitle(sentenceLessonDTO.getTitle());
            lesson.setType(sentenceLessonDTO.getType());
            if (sentenceLessonDTO.getSentences() != null) {
                lesson.setSentences(sentenceLessonDTO.getSentences().stream().map(this::toEntity).collect(Collectors.toList()));
            }
            return lesson;
        }
        // Add other lesson types here in the future
        throw new IllegalArgumentException("Unknown lesson DTO type: " + dto.getClass().getSimpleName());
    }

    public LessonDTO toDto(Lesson entity) {
        if (entity instanceof VocabularyLesson vocabEntity) {
            VocabularyLessonDTO dto = new VocabularyLessonDTO();
            dto.setId(vocabEntity.getId());
            dto.setTitle(vocabEntity.getTitle());
            dto.setType(vocabEntity.getType());
            dto.setItems(vocabEntity.getVocabularyItems().stream().map(this::toDto).collect(Collectors.toList()));
            return dto;
        }
        else if(entity instanceof SentenceLesson sentenceEntity) {
            SentenceLessonDTO dto = new SentenceLessonDTO();
            dto.setId(sentenceEntity.getId());
            dto.setTitle(sentenceEntity.getTitle());
            dto.setType(sentenceEntity.getType());
            dto.setSentences(sentenceEntity.getSentences().stream().map(this::toDto).collect(Collectors.toList()));
        }
        throw new IllegalArgumentException("Unknown lesson entity type: " + entity.getClass().getSimpleName());
    }

    /* **************************** */
    /* ** Vocabulary Item Mapper ** */
    /* **************************** */

    public VocabularyItem toEntity(VocabularyItemDTO dto) {
        VocabularyItem item = new VocabularyItem();
        item.setId(dto.getId());
        item.setWord(dto.getWord());
        item.setTranslation(dto.getTranslation());
        return item;
    }

    public VocabularyItemDTO toDto(VocabularyItem entity) {
        VocabularyItemDTO dto = new VocabularyItemDTO();
        dto.setId(entity.getId());
        dto.setWord(entity.getWord());
        dto.setTranslation(entity.getTranslation());
        return dto;
    }

    /* ********************* */
    /* ** Sentence Mapper ** */
    /* ********************* */

    public Sentence toEntity(SentenceDTO dto) {
        Sentence sentence = new Sentence();
        sentence.setId(dto.getId());
        sentence.setSentence(dto.getSentence());
        sentence.setTranslation(dto.getTranslation());
        return sentence;
    }

    public SentenceDTO toDto(Sentence entity) {
        SentenceDTO dto = new SentenceDTO();
        dto.setId(entity.getId());
        dto.setSentence(entity.getSentence());
        dto.setTranslation(entity.getTranslation());
        return dto;
    }
}