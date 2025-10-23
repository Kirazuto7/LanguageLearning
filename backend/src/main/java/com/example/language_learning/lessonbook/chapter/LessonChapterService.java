package com.example.language_learning.lessonbook.chapter;

import com.example.language_learning.lessonbook.LessonBook;
import com.example.language_learning.user.User;
import com.example.language_learning.shared.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonChapterService {
    private final LessonChapterRepository lessonChapterRepository;
    private final DtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public Optional<LessonChapter> getChapter(Long chapterId) {
        return lessonChapterRepository.findById(chapterId);
    }

    @Transactional(readOnly = true)
    public LessonChapterDTO getChapterDtoByIdAndUser(Long chapterId, User user) {
        return lessonChapterRepository.findByIdAndUserWithPages(chapterId, user)
                .map(dtoMapper::toDto) // .orElse(null);
                .orElseThrow(() -> new RuntimeException("Chapter not found or user does not have access."));
    }

    @Transactional
    public LessonChapter createChapter(LessonBook lessonBook, String title, String nativeTitle) {
        LessonChapter lessonChapter = LessonChapter.builder()
                .lessonBook(lessonBook)
                .title(title)
                .nativeTitle(nativeTitle)
                .build();
        lessonBook.addLessonChapter(lessonChapter);
        return lessonChapterRepository.save(lessonChapter);
    }
    @Transactional
    public LessonChapter updateChapter(Long chapterId, String title, String nativeTitle) {
        LessonChapter lessonChapter = getChapter(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found."));
        if (title != null && !title.isBlank()) {
            lessonChapter.setTitle(title);
        }
        if (nativeTitle != null && !nativeTitle.isBlank()) {
            lessonChapter.setNativeTitle(nativeTitle);
        }
        return lessonChapterRepository.save(lessonChapter);
    }

    @Transactional
    public LessonChapter saveChapter(LessonChapter lessonChapter) {
        return lessonChapterRepository.save(lessonChapter);
    }

    @Transactional
    public void deleteChapter(Long chapterId) {
        lessonChapterRepository.deleteChapterById(chapterId);
    }
}
