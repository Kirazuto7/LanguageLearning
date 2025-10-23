package com.example.language_learning.lessonbook;

import com.example.language_learning.user.User;

import java.util.Optional;

public interface LessonBookRepositoryCustom {

    /**
     * Finds a LessonBook by its ID, fetching all related entities
     * (chapters, pages, and lessons) in a single, efficient query using jOOQ.
     * @param id The ID of the LessonBook to find.
     * @return an Optional containing the fully populated LessonBook, or an empty Optional if not found.
     */
    Optional<LessonBook> findDetailsById(Long id, User user);

    Optional<LessonBook> findDetailsByUserAndLanguageAndDifficulty(User user, String language, String difficulty);

    int deleteLessonBookById(Long lessonBookId, User user);
}
