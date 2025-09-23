package com.example.language_learning.storybook.shortstory.page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryPageRepository extends JpaRepository<StoryPage, Long> {

    @Query("SELECT sp FROM StoryPage sp LEFT JOIN FETCH sp.paragraphs WHERE sp.id = :id")
    Optional<StoryPage> findByIdWithParagraphs(@Param("id") Long id);

    @Query("SELECT sp FROM StoryPage sp LEFT JOIN FETCH sp.vocabulary WHERE sp.id = :id")
    Optional<StoryPage> findByIdWithVocabulary(@Param("id") Long id);

    @Query("SELECT MAX(sp.pageNumber) FROM StoryPage sp WHERE sp.storyBook.id = :id")
    Optional<Integer> findMaxPageNumberByBookId(@Param("id") Long id);
}
