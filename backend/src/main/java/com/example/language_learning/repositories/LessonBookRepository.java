package com.example.language_learning.repositories;

import com.example.language_learning.entity.User;
import com.example.language_learning.entity.models.LessonBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonBookRepository extends JpaRepository<LessonBook, Long> {
    Optional<LessonBook> findByUserAndLanguageAndDifficulty(User user, String language, String difficulty);
}
