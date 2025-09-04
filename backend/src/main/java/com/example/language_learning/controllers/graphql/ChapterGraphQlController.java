package com.example.language_learning.controllers.graphql;

import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.dto.progress.ProgressUpdateDTO;
import com.example.language_learning.entity.user.User;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.responses.GenerationResponse;
import com.example.language_learning.services.ChapterService;
import com.example.language_learning.services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChapterGraphQlController {
    private final ChapterService chapterService;
    private final ProgressService progressService;

    @QueryMapping
    public ChapterDTO getChapterById(@Argument Long id, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new SecurityException("Authentication is required to fetch a chapter.");
        }
        return chapterService.getChapterById(id, user);
    }

    @MutationMapping
    public GenerationResponse generateChapter(@Argument ChapterGenerationRequest request, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new SecurityException("Authentication is required to generate a chapter.");
        }
        GenerationResponse response = chapterService.prepareChapterGeneration(request, user.getId());
        chapterService.generateChapterAsync(request, user.getId(), response.taskId(), response.chapter().id());
        return response;
    }

    @SubscriptionMapping
    public Publisher<ProgressUpdateDTO> chapterGenerationProgress(@Argument String taskId, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new SecurityException("Authentication is required to subscribe to chapter generation progress updates.");
        }

        return progressService.getPublisher()
                .filter(update -> taskId.equals(update.taskId()));
    }
}
