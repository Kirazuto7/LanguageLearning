package com.example.language_learning.ai.inputs;

import com.example.language_learning.user.data.User;
import com.example.language_learning.shared.requests.ChapterGenerationRequest;

/**
 * An immutable record holding all inputs for the chapter preparation workflow.
 */
public record ChapterPrepInput(
    ChapterGenerationRequest request,
    User user
) {}
