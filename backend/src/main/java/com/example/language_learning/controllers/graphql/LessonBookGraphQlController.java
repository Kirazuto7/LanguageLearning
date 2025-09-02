package com.example.language_learning.controllers.graphql;

import com.example.language_learning.dto.models.LessonBookDTO;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.requests.LessonBookRequest;
import com.example.language_learning.services.LessonBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LessonBookGraphQlController {
    private final LessonBookService lessonBookService;

    @QueryMapping
    public List<LessonBookDTO> getLessonBooks(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new SecurityException("Authentication is required to fetch lesson books.");
        }
        return lessonBookService.fetchUserLessonBooks(user.getId());
    }

    @QueryMapping
    public LessonBookDTO getLessonBook(@Argument LessonBookRequest request, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new SecurityException("Authentication is required to fetch a lesson book.");
        }
        return lessonBookService.fetchLessonBook(request, user.getId());
    }
}
