package com.example.language_learning.lessonbook;

import com.example.language_learning.user.User;
import com.example.language_learning.lessonbook.requests.LessonBookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
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
        if (request == null) {
            throw new IllegalArgumentException("LessonBookRequest input is required.");
        }
        return lessonBookService.findOrCreateBookDTO(request, user);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public LessonBookDTO getLessonBookById(@Argument Long id, @AuthenticationPrincipal User user) {
        return lessonBookService.getLessonBookById(id, user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean deleteLessonBook(@Argument Long id, @AuthenticationPrincipal User user) {
        return lessonBookService.deleteLessonBook(id, user);
    }
}
