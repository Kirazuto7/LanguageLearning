package com.example.language_learning.storybook;

import com.example.language_learning.user.User;

import java.util.Optional;

public interface StoryBookRepositoryCustom {
    /**
     * Finds a StoryBook by its ID< fetching all related entities
     * (short stories, pages, paragraphs, and vocabulary in a single, efficient query using JOOQ.
     * @param id The ID of the StoryBook to find.
     * @return an Optional containing the fully populated StoryBook, or an empty Optional if not found.
     */
    Optional<StoryBook> findStoryBookDetailsById(Long id, User user);

    int deleteStoryBookById(Long storyBookId, User user);
}
