package com.example.language_learning.repositories;

import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonBookRepository extends JpaRepository<LessonBook, Long> {
    @Query("SELECT b FROM LessonBook b LEFT JOIN FETCH b.chapters c LEFT JOIN FETCH c.pages WHERE b.user = :user AND b.language = :language AND b.difficulty = :difficulty")
    Optional<LessonBook> findByUserAndLanguageAndDifficulty(@Param("user") User user, @Param("language") String language, @Param("difficulty") String difficulty);

    List<LessonBook> findAllByUser(User user);
}
