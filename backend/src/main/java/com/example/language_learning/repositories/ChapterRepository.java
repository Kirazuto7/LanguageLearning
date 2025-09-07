package com.example.language_learning.repositories;

import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Optional<Chapter> findByIdAndLessonBook_User(Long id, User user);

    @Query("SELECT c FROM Chapter c JOIN FETCH c.pages WHERE c.id = :id AND c.lessonBook.user = :user")
    Optional<Chapter> findByIdAndUserWithPages(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT c FROM Chapter c LEFT JOIN FETCH c.pages WHERE c.id = :id")
    Optional<Chapter> findByIdWithPages(@Param("id") Long id);
}

