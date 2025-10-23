package com.example.language_learning.lessonbook.chapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.example.language_learning.generated.jooq.tables.LessonChapter.LESSON_CHAPTER;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LessonChapterRepositoryCustomImpl implements LessonChapterRepositoryCustom {
    private final DSLContext dsl;

    @Override
    public void deleteChapterById(Long chapterId) {
        dsl.deleteFrom(LESSON_CHAPTER)
                .where(LESSON_CHAPTER.ID.eq(chapterId))
                .execute();
        log.info("Deleted chapter with ID: {}", chapterId);
    }
}
