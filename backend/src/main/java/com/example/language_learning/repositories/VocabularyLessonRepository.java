package com.example.language_learning.repositories;

import com.example.language_learning.entity.lessons.VocabularyLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyLessonRepository extends JpaRepository<VocabularyLesson, Long> {
}

