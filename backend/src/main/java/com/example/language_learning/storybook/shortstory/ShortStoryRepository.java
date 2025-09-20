package com.example.language_learning.storybook.shortstory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortStoryRepository extends JpaRepository<ShortStory, Long> {

    @Query("SELECT ss FROM ShortStory ss LEFT JOIN FETCH ss.storyPages WHERE ss.id = :id")
    Optional<ShortStory> findByIdWithPages(@Param("id") Long id);
}
