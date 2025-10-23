package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortStoryRepository extends JpaRepository<ShortStory, Long>, ShortStoryRepositoryCustom {

    @Query("SELECT ss FROM ShortStory ss " +
           "LEFT JOIN FETCH ss.storyPages sp " +
           "WHERE ss.id = :shortStoryId AND ss.storyBook.user = :user")
    Optional<ShortStory> findByIdAndUserWithPagesOnly(@Param("shortStoryId") Long shortStoryId, @Param("user") User user);

    @Query("SELECT ss FROM ShortStory ss LEFT JOIN FETCH ss.storyPages WHERE ss.id = :id")
    Optional<ShortStory> findByIdWithPagesOnly(@Param("id") Long id);

}
