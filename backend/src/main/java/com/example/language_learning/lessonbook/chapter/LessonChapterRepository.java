package com.example.language_learning.lessonbook.chapter;

import com.example.language_learning.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonChapterRepository extends JpaRepository<LessonChapter, Long>, LessonChapterRepositoryCustom {
    @Query("SELECT c FROM LessonChapter c JOIN FETCH c.lessonPages WHERE c.id = :id AND c.lessonBook.user = :user")
    Optional<LessonChapter> findByIdAndUserWithPages(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT c FROM LessonChapter c LEFT JOIN FETCH c.lessonPages WHERE c.id = :id")
    Optional<LessonChapter> findByIdWithPages(@Param("id") Long id);
}

