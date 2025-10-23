package com.example.language_learning.lessonbook.chapter.lesson.page.question;

import com.example.language_learning.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonQuestionRepository extends JpaRepository<LessonQuestion, Long> {

    @Query("SELECT q FROM LessonQuestion q WHERE q.id = :questionId AND q.lesson.lessonPage.lessonChapter.lessonBook.user = :user")
    Optional<LessonQuestion> findByIdAndUser(@Param("questionId") Long questionId, @Param("user") User user);
}