package com.example.language_learning.repositories;

import com.example.language_learning.entity.lessons.GrammarLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrammarLessonRepository extends JpaRepository<GrammarLesson, Long> {
    // You can add custom query methods specific to GrammarLesson here
}