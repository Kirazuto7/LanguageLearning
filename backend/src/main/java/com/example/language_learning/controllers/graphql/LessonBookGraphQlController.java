package com.example.language_learning.controllers.graphql;

import com.example.language_learning.dto.models.LessonBookDTO;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.requests.LessonBookRequest;
import com.example.language_learning.services.LessonBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LessonBookGraphQlController {
    private final LessonBookService lessonBookService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<LessonBookDTO> getLessonBooks(@AuthenticationPrincipal User user) {
        return lessonBookService.fetchUserLessonBooks(user);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public LessonBookDTO getLessonBook(@Argument LessonBookRequest request, @AuthenticationPrincipal User user) {
        return lessonBookService.findOrCreateBookDTO(request, user);
    }
}
