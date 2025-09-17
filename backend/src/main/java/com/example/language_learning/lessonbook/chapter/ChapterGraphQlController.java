package com.example.language_learning.lessonbook.chapter;

import com.example.language_learning.lessonbook.chapter.dtos.ChapterDTO;
import com.example.language_learning.shared.dtos.progress.ProgressUpdateDTO;
import com.example.language_learning.user.data.User;
import com.example.language_learning.shared.requests.ChapterGenerationRequest;
import com.example.language_learning.shared.responses.GenerationResponse;
import com.example.language_learning.shared.services.ProgressService;
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

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChapterGraphQlController {
    private final ChapterGenerationService chapterGenerationService;
    private final ChapterService chapterService;
    private final ProgressService progressService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public ChapterDTO getChapterById(@Argument Long id, @AuthenticationPrincipal User user) {
        return chapterService.getChapterDtoByIdAndUser(id, user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public GenerationResponse generateChapter(@Argument ChapterGenerationRequest request, @AuthenticationPrincipal User user) {
        return chapterGenerationService.generateChapter(request, user);
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Publisher<ProgressUpdateDTO> chapterGenerationProgress(@Argument String taskId, @AuthenticationPrincipal User user) {
        return progressService.getPublisher(taskId)
                .filter(update -> taskId.equals(update.taskId()))
                .doOnSubscribe(sub -> log.info("User {} subscribed to {}", user.getUsername(), taskId))
                .takeUntil(progressUpdate -> progressUpdate.isComplete() || progressUpdate.error() != null)
                .doFinally(signal -> log.info("User {} unsubscribed from {}", user.getUsername(), taskId));
    }
}
