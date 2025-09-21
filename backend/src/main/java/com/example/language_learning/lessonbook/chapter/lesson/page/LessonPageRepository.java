package com.example.language_learning.lessonbook.chapter.lesson.page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonPageRepository extends JpaRepository<LessonPage, Long> {

    @Query("SELECT MAX(p.pageNumber) FROM LessonPage p WHERE p.lessonChapter.lessonBook.id = :bookId")
    Optional<Integer> findMaxPageNumberByBookId(@Param("bookId") Long bookId);
}

