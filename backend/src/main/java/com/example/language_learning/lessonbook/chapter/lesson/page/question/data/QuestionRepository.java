package com.example.language_learning.lessonbook.chapter.lesson.page.question.data;

import com.example.language_learning.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.id = :questionId AND q.lesson.page.chapter.lessonBook.user = :user")
    Optional<Question> findByIdAndUser(@Param("questionId") Long questionId, @Param("user") User user);
}