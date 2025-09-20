package com.example.language_learning.storybook.shortstory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortStoryRepository extends JpaRepository<ShortStory, Long> {

    @Query("SELECT sc FROM ShortStory sc LEFT JOIN FETCH sc.storyPages WHERE sc.id = :id")
    Optional<ShortStory> findByIdWithPages(@Param("id") Long id);
}
