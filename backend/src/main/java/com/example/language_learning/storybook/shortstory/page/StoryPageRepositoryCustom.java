package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.storybook.shortstory.ShortStory;

import java.util.List;

public interface StoryPageRepositoryCustom {
    void batchInsertPages(ShortStory shortStory, List<StoryPage> storyPages);
}
