package com.example.language_learning.ai.inputs;

import com.example.language_learning.entity.user.User;
import com.example.language_learning.requests.ChapterGenerationRequest;

/**
 * An immutable record holding all inputs for the chapter preparation workflow.
 */
public record ChapterPrepInput(
    ChapterGenerationRequest request,
    User user
) {}
