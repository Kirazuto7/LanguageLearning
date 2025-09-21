package com.example.language_learning.storybook.shortstory.page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryPageService {
    private final StoryPageRepository storyPageRepository;

    public int getLastPageForBook(Long id) {
        return storyPageRepository.findMaxPageNumberByBookId(id).orElse(0);
    }
}
