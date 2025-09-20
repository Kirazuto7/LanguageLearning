package com.example.language_learning.ai.contexts;

import com.example.language_learning.lessonbook.chapter.LessonChapter;
import com.example.language_learning.lessonbook.requests.ChapterGenerationRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The context object that holds all the data that persists across all states of the chapter generation process.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ChapterGenerationContext {
    private final ChapterGenerationRequest request;
    private final String taskId;
    private final Long chapterId;
    private final AtomicInteger pageCounter;
    private LessonChapter lessonChapter;
}