package com.example.language_learning.lessonbook;

import com.example.language_learning.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonBookRepository extends JpaRepository<LessonBook, Long>, LessonBookRepositoryCustom {
    // Chapter page's will be loaded lazily to avoid MultipleBagFetchException
    @Query("SELECT b FROM LessonBook b LEFT JOIN FETCH b.lessonChapters WHERE b.user = :user AND b.language = :language AND b.difficulty = :difficulty")
    Optional<LessonBook> findByUserAndLanguageAndDifficulty(@Param("user") User user, @Param("language") String language, @Param("difficulty") String difficulty);

    List<LessonBook> findAllByUser(User user);
}
