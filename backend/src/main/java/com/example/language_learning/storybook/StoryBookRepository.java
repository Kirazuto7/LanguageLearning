package com.example.language_learning.storybook;

import com.example.language_learning.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryBookRepository extends JpaRepository<StoryBook, Long> {

    // Story page's will be loaded lazily to avoid MultipleBagFetchException
    @Query("SELECT sb FROM StoryBook sb LEFT JOIN FETCH sb.shortStories WHERE sb.user = :user AND sb.language = :language AND sb.difficulty = :difficulty")
    Optional<StoryBook> findByUserAndLanguageAndDifficulty(@Param("user") User user, @Param("language") String language, @Param("difficulty") String difficulty);

    List<StoryBook> findAllByUser(User user);
}
