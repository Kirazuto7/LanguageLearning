package com.example.language_learning.mapper;

import com.example.language_learning.dto.BookDTO;
import com.example.language_learning.dto.ChapterDTO;
import com.example.language_learning.dto.LessonDTO;
import com.example.language_learning.dto.PageDTO;
import com.example.language_learning.dto.VocabularyItemDTO;
import com.example.language_learning.dto.VocabularyLessonDTO;
import com.example.language_learning.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    // Book Mapper
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


    // Chapter Mapper
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

    // Page Mapper
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

    // Lesson Mapper
    public Lesson toEntity(LessonDTO dto) {
        if (dto instanceof VocabularyLessonDTO) {
            VocabularyLessonDTO vocabDto = (VocabularyLessonDTO) dto;
            VocabularyLesson lesson = new VocabularyLesson();
            lesson.setId(vocabDto.getId());
            lesson.setTitle(vocabDto.getTitle());
            lesson.setType(vocabDto.getType());
            if (vocabDto.getItems() != null) {
                lesson.setVocabularyItems(vocabDto.getItems().stream().map(this::toEntity).collect(Collectors.toList()));
            }
            return lesson;
        }
        // Add other lesson types here in the future
        throw new IllegalArgumentException("Unknown lesson DTO type: " + dto.getClass().getSimpleName());
    }

    public LessonDTO toDto(Lesson entity) {
        if (entity instanceof VocabularyLesson) {
            VocabularyLesson vocabEntity = (VocabularyLesson) entity;
            VocabularyLessonDTO dto = new VocabularyLessonDTO();
            dto.setId(vocabEntity.getId());
            dto.setTitle(vocabEntity.getTitle());
            dto.setType(vocabEntity.getType());
            dto.setItems(vocabEntity.getVocabularyItems().stream().map(this::toDto).collect(Collectors.toList()));
            return dto;
        }
        throw new IllegalArgumentException("Unknown lesson entity type: " + entity.getClass().getSimpleName());
    }

    // Vocabulary Item Mapper
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
}