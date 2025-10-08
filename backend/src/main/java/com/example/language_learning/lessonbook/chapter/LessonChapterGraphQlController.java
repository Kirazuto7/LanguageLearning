package com.example.language_learning.lessonbook.chapter;

import com.example.language_learning.shared.dtos.progress.ProgressUpdateDTO;
import com.example.language_learning.user.User;
import com.example.language_learning.lessonbook.requests.ChapterGenerationRequest;
import com.example.language_learning.lessonbook.responses.GenerationResponse;
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
public class LessonChapterGraphQlController {
    private final LessonChapterGenerationService lessonChapterGenerationService;
    private final LessonChapterService lessonChapterService;
    private final ProgressService progressService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public LessonChapterDTO getChapterById(@Argument Long id, @AuthenticationPrincipal User user) {
        return lessonChapterService.getChapterDtoByIdAndUser(id, user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public GenerationResponse generateChapter(@Argument ChapterGenerationRequest request, @AuthenticationPrincipal User user) {
        return lessonChapterGenerationService.generateChapter(request, user);
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Publisher<ProgressUpdateDTO> chapterGenerationProgress(@Argument String taskId, @AuthenticationPrincipal User user) {
        return progressService.getPublisher(taskId)
                .filter(update -> taskId.equals(update.taskId()))
                .doOnSubscribe(sub -> log.info("User {} subscribed to Lesson Chapter Generation Task: {}", user.getUsername(), taskId))
                .takeUntil(progressUpdate -> progressUpdate.isComplete() || progressUpdate.error() != null)
                .doFinally(signal -> log.debug("User {} unsubscribed from {} due to signal: {}", user.getUsername(), taskId, signal))
                .doOnCancel(() -> log.info("Client cancelled subscription for task {}. Stream is being terminated.", taskId));
    }
}
