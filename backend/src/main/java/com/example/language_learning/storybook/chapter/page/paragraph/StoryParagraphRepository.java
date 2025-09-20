package com.example.language_learning.storybook.chapter.page.paragraph;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryParagraphRepository extends JpaRepository<StoryParagraph, Long> {
}
