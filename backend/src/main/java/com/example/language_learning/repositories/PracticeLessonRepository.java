package com.example.language_learning.repositories;

import com.example.language_learning.entity.lessons.PracticeLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeLessonRepository extends JpaRepository<PracticeLesson, Long> {
}
