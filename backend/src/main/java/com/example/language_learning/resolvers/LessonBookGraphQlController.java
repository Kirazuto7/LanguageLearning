package com.example.language_learning.resolvers;

import com.example.language_learning.entity.models.LessonBook;
import com.example.language_learning.repositories.LessonBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LessonBookGraphQlController {
    private final LessonBookRepository lessonBookRepository;

    @QueryMapping
    public List<LessonBook> getLessonBooks() {
        return lessonBookRepository.findAll();
    }
}
