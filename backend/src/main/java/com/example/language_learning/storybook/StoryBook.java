package com.example.language_learning.storybook;

import com.example.language_learning.shared.data.BaseBook;
import com.example.language_learning.storybook.chapter.StoryChapter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "story_books")
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StoryBook extends BaseBook {
    private String genre;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "storyBook", fetch = FetchType.LAZY)
    @OrderBy("chapterNumber ASC")
    @Builder.Default
    private List<StoryChapter> storyChapters = new ArrayList<>();

    public void addStoryChapter(StoryChapter storyChapter) {
        storyChapters.add(storyChapter);
        storyChapter.setStoryBook(this);
    }
}
