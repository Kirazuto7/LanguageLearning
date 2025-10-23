package com.example.language_learning.lessonbook.chapter.lesson.page;

import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.lessonbook.chapter.LessonChapterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonPageService {

    private final LessonPageRepository lessonPageRepository;
    private final LessonChapterRepository lessonChapterRepository;

    /**
     * Creates a new Page entity, links it to a lessonChapter and a lesson,
     * and persists it to the database in a new transaction.
     */
    @Transactional
    public LessonPage createAndPersistPage(LessonChapter lessonChapter, Lesson lesson) {
       LessonChapter managedLessonChapter = lessonChapterRepository.findByIdWithPages(lessonChapter.getId())
               .orElseThrow(() -> new RuntimeException("Chapter not found during page creation: " + lessonChapter.getId()));
       LessonPage lessonPage = LessonPage.builder()
               .lesson(lesson)
               .lessonChapter(managedLessonChapter)
               .build();
       if (lesson != null) {
           lesson.setLessonPage(lessonPage);
       }
       managedLessonChapter.addLessonPage(lessonPage);
       return lessonPageRepository.save(lessonPage);
    }

    @Transactional
    public void batchCreateAndPersistPages(LessonChapter lessonChapter, List<Lesson> lessons) {
        LessonChapter managedLessonChapter = lessonChapterRepository.findByIdWithPages(lessonChapter.getId())
                .orElseThrow(() -> new RuntimeException("Chapter not found during page creation: " + lessonChapter.getId()));

        List<LessonPage> lessonPages = lessons.stream()
                .map(lesson -> {
                    LessonPage lessonPage = LessonPage.builder()
                            .lesson(lesson)
                            .lessonChapter(managedLessonChapter)
                            .build();
                    if (lesson != null) {
                        lesson.setLessonPage(lessonPage);
                    }
                    return lessonPage;
                })
                .collect(Collectors.toList());
        lessonPageRepository.batchInsertPages(managedLessonChapter, lessonPages);
    }
}
