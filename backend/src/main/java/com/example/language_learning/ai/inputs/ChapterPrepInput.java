package com.example.language_learning.ai.inputs;

import com.example.language_learning.user.User;
import com.example.language_learning.lessonbook.requests.ChapterGenerationRequest;

/**
 * An immutable record holding all inputs for the lessonChapter preparation workflow.
 */
public record ChapterPrepInput(
    ChapterGenerationRequest request,
    User user
) {}
