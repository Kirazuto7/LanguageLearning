package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonSentenceRepository extends JpaRepository<LessonSentence, Long> {
}