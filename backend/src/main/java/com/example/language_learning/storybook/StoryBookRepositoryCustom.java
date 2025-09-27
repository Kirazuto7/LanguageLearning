package com.example.language_learning.storybook;

import com.example.language_learning.user.User;

import java.util.Optional;

public interface StoryBookRepositoryCustom {
    Optional<StoryBook> findByUserAndLanguageAndDifficultyWithJOOQ(User user, String language, String difficulty);
}
