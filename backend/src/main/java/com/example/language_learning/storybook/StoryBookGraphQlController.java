package com.example.language_learning.storybook;

import com.example.language_learning.shared.dtos.progress.ProgressUpdateDTO;
import com.example.language_learning.shared.services.ProgressService;
import com.example.language_learning.storybook.requests.ShortStoryGenerationRequest;
import com.example.language_learning.storybook.requests.StoryBookRequest;
import com.example.language_learning.storybook.responses.StoryGenerationResponse;
import com.example.language_learning.storybook.shortstory.StoryGenerationService;
import com.example.language_learning.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StoryBookGraphQlController {
    private final StoryBookService storyBookService;
    private final StoryGenerationService storyGenerationService;
    private final ProgressService progressService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public StoryBookDTO getStoryBook(@Argument StoryBookRequest request,  @AuthenticationPrincipal User user) {
        if (request == null) {
            throw new IllegalArgumentException("StoryBookRequest input is required.");
        }
        return storyBookService.findOrCreateBookDTO(request, user);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<StoryBookDTO> getStoryBooks(@AuthenticationPrincipal User user) {
        return storyBookService.fetchUserStoryBooks(user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public StoryGenerationResponse generateShortStory(@Argument ShortStoryGenerationRequest request, @AuthenticationPrincipal User user) {
        return storyGenerationService.initiateShortStoryGeneration(request, user);
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Publisher<ProgressUpdateDTO> shortStoryGenerationProgress(@Argument String taskId, @AuthenticationPrincipal User user) {
        return progressService.getPublisher(taskId)
                .filter(update -> taskId.equals(update.taskId()))
                .doOnSubscribe(sub -> log.info("User {} subscribed to {}", user.getUsername(), taskId))
                .takeUntil(progressUpdate -> progressUpdate.isComplete() || progressUpdate.error() != null)
                .doFinally(signal -> log.info("User {} unsubscribed from {}", user.getUsername(), taskId));
    }
}
