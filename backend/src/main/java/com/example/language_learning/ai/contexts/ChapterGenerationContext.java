package com.example.language_learning.ai.contexts;

import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.chapter.lesson.data.Lesson;
import com.example.language_learning.lessonbook.requests.ChapterGenerationRequest;
import com.example.language_learning.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * The context object that holds all the data that persists across all states of the lessonChapter generation process.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ChapterGenerationContext {
    private final ChapterGenerationRequest request;
    private final String taskId;
    private final Long chapterId;
    private final User user;
    private LessonChapter lessonChapter;
    private List<Lesson> lessonsToPersist = new ArrayList<>();
}