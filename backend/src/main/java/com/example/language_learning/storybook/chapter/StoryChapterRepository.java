package com.example.language_learning.storybook.chapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryChapterRepository extends JpaRepository<StoryChapter, Long> {

    @Query("SELECT sc FROM StoryChapter sc LEFT JOIN FETCH sc.storyPages WHERE sc.id = :id")
    Optional<StoryChapter> findByIdWithPages(@Param("id") Long id);
}
