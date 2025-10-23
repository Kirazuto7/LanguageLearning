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
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

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
    public StoryBookDTO getStoryBookById(@Argument Long id, @AuthenticationPrincipal User user) {
        return storyBookService.getStoryBookById(id, user);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<StoryBookDTO> getStoryBooks(@AuthenticationPrincipal User user) {
        return storyBookService.fetchUserStoryBooks(user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean deleteStoryBook(@Argument Long id, @AuthenticationPrincipal User user) {
        return storyBookService.deleteStoryBook(id, user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public StoryGenerationResponse generateShortStory(@Argument ShortStoryGenerationRequest request, @AuthenticationPrincipal User user) {
        return storyGenerationService.initiateShortStoryGeneration(request, user);
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<ProgressUpdateDTO> shortStoryGenerationProgress(@Argument String taskId, @AuthenticationPrincipal User user) {
        return progressService.getProgressUpdate(taskId, user)
                .doOnSubscribe(sub -> {
                    log.info("User {} subscribed to Short Story Generation Task: {}", user.getUsername(), taskId);
                    var latestUpdate = progressService.getLatestUpdate(taskId);
                    if (latestUpdate != null && latestUpdate.userId().equals(user.getId())) {
                        var sink = progressService.getSink(taskId);
                        if (sink != null) {
                            log.info("Emitting latest cached update for task {} to subscriber.", taskId);
                            sink.tryEmitNext(latestUpdate.update());
                        }
                    }
                })
                .filter(update -> taskId.equals(update.taskId()))
                .onErrorResume(error -> {
                    log.debug("Suppressing original onError for task {} after sending error DTO.", taskId);
                    return Flux.empty();
                })
                .takeUntil(progressUpdate -> progressUpdate.isComplete() || progressUpdate.isError())
                .doFinally(signal -> log.debug("User {} unsubscribed from {} due to signal: {}", user.getUsername(), taskId, signal))
                .doOnCancel(() -> log.info("Client cancelled subscription for task {}. Stream is being terminated.", taskId));
    }
}
