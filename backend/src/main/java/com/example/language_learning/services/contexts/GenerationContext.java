package com.example.language_learning.services.contexts;

import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.requests.ChapterGenerationRequest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The context object that holds all the data that persists across all states of the chapter generation process.
 */
public record GenerationContext(
        ChapterGenerationRequest request,
        String taskId,
        Chapter chapter,
        AtomicInteger pageCounter
) {}