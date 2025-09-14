package com.example.language_learning.controllers.graphql;

import com.example.language_learning.entity.user.User;
import com.example.language_learning.requests.PracticeLessonCheckRequest;
import com.example.language_learning.responses.PracticeLessonCheckResponse;
import com.example.language_learning.services.ProofreadService;
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
