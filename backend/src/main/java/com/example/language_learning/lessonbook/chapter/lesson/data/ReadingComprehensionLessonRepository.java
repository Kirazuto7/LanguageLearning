package com.example.language_learning.lessonbook.chapter.lesson.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingComprehensionLessonRepository extends JpaRepository<ReadingComprehensionLesson, Long> {
    // You can add custom query methods specific to ReadingComprehensionLesson here
}
