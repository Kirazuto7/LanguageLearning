package com.example.language_learning.services.contexts;

import com.example.language_learning.entity.user.User;
import com.example.language_learning.requests.PracticeLessonCheckRequest;

public record ProofreadContext(
        PracticeLessonCheckRequest request,
        User user
) {}