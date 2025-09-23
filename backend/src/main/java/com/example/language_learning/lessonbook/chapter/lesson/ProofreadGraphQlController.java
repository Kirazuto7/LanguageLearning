package com.example.language_learning.lessonbook.chapter.lesson;

import com.example.language_learning.user.User;
import com.example.language_learning.lessonbook.chapter.lesson.requests.PracticeLessonCheckRequest;
import com.example.language_learning.lessonbook.chapter.lesson.responses.PracticeLessonCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ProofreadGraphQlController {

    private final ProofreadService proofreadService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<PracticeLessonCheckResponse> proofreadPracticeSentence(@Argument PracticeLessonCheckRequest request, @AuthenticationPrincipal User user) {
        return proofreadService.checkSentence(request, user);
    }
}
