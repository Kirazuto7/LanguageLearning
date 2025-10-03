package com.example.language_learning.storybook.shortstory.page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryPageRepository extends JpaRepository<StoryPage, Long>, StoryPageRepositoryCustom {

    @Query("SELECT sp FROM StoryPage sp LEFT JOIN FETCH sp.paragraphs WHERE sp.id = :id")
    Optional<StoryPage> findByIdWithParagraphs(@Param("id") Long id);

    @Query("SELECT sp FROM StoryPage sp LEFT JOIN FETCH sp.vocabulary WHERE sp.id = :id")
    Optional<StoryPage> findByIdWithVocabulary(@Param("id") Long id);

    @Query("SELECT DISTINCT sp FROM StoryPage sp LEFT JOIN FETCH sp.paragraphs WHERE sp.shortStory.id = :storyId")
    List<StoryPage> loadPagesWithParagraphs(@Param("storyId") Long storyId);

    @Query("SELECT DISTINCT sp FROM StoryPage sp LEFT JOIN FETCH sp.vocabulary WHERE sp.shortStory.id = :storyId")
    List<StoryPage> loadPagesWithVocabulary(@Param("storyId") Long storyId);

    @Query("SELECT DISTINCT sp FROM StoryPage sp LEFT JOIN FETCH sp.paragraphs WHERE sp.shortStory.id IN :storyIds")
    List<StoryPage> loadPagesWithParagraphsIn(@Param("storyIds") List<Long> storyIds);

    @Query("SELECT DISTINCT sp FROM StoryPage sp LEFT JOIN FETCH sp.vocabulary WHERE sp.shortStory.id IN :storyIds")
    List<StoryPage> loadPagesWithVocabularyIn(@Param("storyIds") List<Long> storyIds);
}
