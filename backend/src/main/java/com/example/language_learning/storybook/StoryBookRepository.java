package com.example.language_learning.storybook;

import com.example.language_learning.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryBookRepository extends JpaRepository<StoryBook, Long>, StoryBookRepositoryCustom {

    @EntityGraph(value = "StoryBook.withShortStories")
    Optional<StoryBook> findByUserAndLanguageAndDifficulty(@Param("user") User user, @Param("language") String language, @Param("difficulty") String difficulty);

    List<StoryBook> findAllByUser(User user);
}
