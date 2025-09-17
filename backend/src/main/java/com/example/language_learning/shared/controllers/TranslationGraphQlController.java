package com.example.language_learning.shared.controllers;

import com.example.language_learning.user.data.User;
import com.example.language_learning.shared.requests.TranslationRequest;
import com.example.language_learning.shared.responses.TranslationResponse;
import com.example.language_learning.shared.services.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class TranslationGraphQlController {

    private final TranslationService translationService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<TranslationResponse> translateText(@Argument TranslationRequest request, @AuthenticationPrincipal User user) {
        return translationService.translateText(request);
    }
}
