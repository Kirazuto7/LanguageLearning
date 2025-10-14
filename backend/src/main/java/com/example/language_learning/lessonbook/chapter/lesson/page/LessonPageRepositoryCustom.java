package com.example.language_learning.lessonbook.chapter.lesson.page;

import com.example.language_learning.lessonbook.chapter.LessonChapter;

import java.util.List;

public interface LessonPageRepositoryCustom {
    void batchInsertPages(LessonChapter chapter, List<LessonPage> lessonPages);
}
