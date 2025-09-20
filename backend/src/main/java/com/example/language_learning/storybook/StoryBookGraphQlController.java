package com.example.language_learning.storybook;

import com.example.language_learning.shared.services.ProgressService;
import com.example.language_learning.storybook.requests.ShortStoryGenerationRequest;
import com.example.language_learning.storybook.responses.StoryGenerationResponse;
import com.example.language_learning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StoryBookGraphQlController {
    private final StoryBookService storyBookService;
    private final ProgressService progressService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public StoryBookDTO getStoryBook(@AuthenticationPrincipal User user) {

    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public StoryGenerationResponse generateShortStory(@Argument ShortStoryGenerationRequest request, @AuthenticationPrincipal User user) {
        return storyBookService.initiateShortStoryGeneration(request, user);
    }
}
