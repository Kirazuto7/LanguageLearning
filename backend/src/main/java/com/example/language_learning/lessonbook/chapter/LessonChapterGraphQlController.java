package com.example.language_learning.lessonbook.chapter;

import com.example.language_learning.shared.dtos.progress.ProgressUpdateDTO;
import com.example.language_learning.user.User;
import com.example.language_learning.lessonbook.requests.ChapterGenerationRequest;
import com.example.language_learning.lessonbook.responses.GenerationResponse;
import com.example.language_learning.shared.services.ProgressService;
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
    public Flux<ProgressUpdateDTO> chapterGenerationProgress(@Argument String taskId, @AuthenticationPrincipal User user) {
        return progressService.getProgressUpdate(taskId, user)
                .doOnSubscribe(sub -> {
                    log.info("User {} subscribed to Lesson Chapter Generation Task: {}", user.getUsername(), taskId);

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
