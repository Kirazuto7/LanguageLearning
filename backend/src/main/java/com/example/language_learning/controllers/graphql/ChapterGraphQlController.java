package com.example.language_learning.controllers.graphql;

import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.dto.progress.ProgressUpdateDTO;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.responses.GenerationResponse;
import com.example.language_learning.services.ChapterService;
import com.example.language_learning.services.ProgressService;
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

import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChapterGraphQlController {
    private final ChapterService chapterService;
    private final ProgressService progressService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public ChapterDTO getChapterById(@Argument Long id, @AuthenticationPrincipal User user) {
        return chapterService.getChapterById(id, user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public GenerationResponse generateChapter(@Argument ChapterGenerationRequest request, @AuthenticationPrincipal User user) {
        GenerationResponse response = chapterService.prepareChapterGeneration(request, user.getId());
        chapterService.generateChapterAsync(request, user.getId(), response.taskId(), response.chapter().id());
        return response;
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Publisher<ProgressUpdateDTO> chapterGenerationProgress(@Argument String taskId, @AuthenticationPrincipal User user) {
        return progressService.getPublisher()
                .filter(update -> taskId.equals(update.taskId()))
                .doOnSubscribe(sub -> log.info("User {} subscribed to {}", user.getUsername(), taskId))
                .doFinally(signal -> log.info("User {} unsubscribed from {}", user.getUsername(), taskId));
    }
}
