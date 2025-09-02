package com.example.language_learning.resolvers;

import com.example.language_learning.dto.models.ChapterDTO;
import com.example.language_learning.dto.progress.ProgressUpdateDTO;
import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.mapper.DtoMapper;
import com.example.language_learning.repositories.ChapterRepository;
import com.example.language_learning.requests.ChapterGenerationRequest;
import com.example.language_learning.responses.GenerationTask;
import com.example.language_learning.services.ChapterService;
import com.example.language_learning.services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChapterGraphQlController {
    private final ChapterRepository chapterRepository;
    private final ChapterService chapterService;
    private final ProgressService progressService;
    private final DtoMapper dtoMapper;

    @QueryMapping
    public ChapterDTO getChapterById(@Argument Long id) {
        Chapter chapter = chapterRepository.findById(id).orElse(null);
        return dtoMapper.toDto(chapter);
    }

    @MutationMapping
    public GenerationTask generateChapter(@Argument ChapterGenerationRequest request) {
        String taskId = UUID.randomUUID().toString();
        chapterService.generateNewChapterStream(request, taskId);
        return new GenerationTask(taskId);
    }

    @SubscriptionMapping
    public Publisher<ProgressUpdateDTO> chapterGenerationProgress(@Argument String taskId) {
        return progressService.getPublisher()
                .filter(update -> taskId.equals(update.taskId()));
    }
}
