package com.example.language_learning.services;

import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.ChapterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final DtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public Optional<Chapter> getChapter(Long chapterId) {
        return chapterRepository.findById(chapterId);
    }

    @Transactional(readOnly = true)
    public ChapterDTO getChapterDtoByIdAndUser(Long chapterId, User user) {
        return chapterRepository.findByIdAndUserWithPages(chapterId, user)
                .map(dtoMapper::toDto)
                .orElse(null);
    }

    @Transactional
    public Chapter createChapter(LessonBook lessonBook, int chapterNumber, String title, String nativeTitle) {
        Chapter chapter = Chapter.builder()
                .lessonBook(lessonBook)
                .chapterNumber(chapterNumber)
                .title(title)
                .nativeTitle(nativeTitle)
                .build();
        lessonBook.addChapter(chapter);
        return chapterRepository.save(chapter);
    }

    @Transactional
    public Chapter updateChapter(Long chapterId, int chapterNumber, String title, String nativeTitle) {
        Chapter chapter = getChapter(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found."));
        if (chapterNumber != 0) {
            chapter.setChapterNumber(chapterNumber);
        }
        if (title != null && !title.isBlank()) {
            chapter.setTitle(title);
        }
        if (nativeTitle != null && !nativeTitle.isBlank()) {
            chapter.setNativeTitle(nativeTitle);
        }
        return chapterRepository.save(chapter);
    }

    @Transactional
    public Chapter saveChapter(Chapter chapter) {
        return chapterRepository.save(chapter);
    }
}
