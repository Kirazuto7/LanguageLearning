package com.example.language_learning.controllers.graphql;

import com.example.language_learning.entity.user.User;
import com.example.language_learning.requests.PracticeLessonCheckRequest;
import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.example.language_learning.services.PracticeLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PracticeLessonGraphQlController {

    private final PracticeLessonService practiceLessonService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<PracticeLessonCheckResponse> checkPracticeSentence(@Argument PracticeLessonCheckRequest request, @AuthenticationPrincipal User user) {
        return practiceLessonService.checkSentence(request, user);
    }
}
