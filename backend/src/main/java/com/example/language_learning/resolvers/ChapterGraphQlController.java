package com.example.language_learning.resolvers;

import com.example.language_learning.entity.models.Chapter;
import com.example.language_learning.repositories.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChapterGraphQlController {
    private final ChapterRepository chapterRepository;

    @QueryMapping
    public Chapter getChapterById(@Argument Long id) {
        return chapterRepository.findById(id).orElse(null);
    }
}
