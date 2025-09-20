package com.example.language_learning.storybook.chapter;

import com.example.language_learning.shared.data.BaseChapter;
import com.example.language_learning.storybook.chapter.page.StoryPage;
import com.example.language_learning.storybook.StoryBook;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "story_chapters")
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StoryChapter extends BaseChapter {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "storyChapter")
    @OrderBy("pageNumber ASC")
    @Builder.Default
    private List<StoryPage> storyPages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "story_book_id")
    private StoryBook storyBook;

    public void addStoryPage(StoryPage storyPage) {
        storyPages.add(storyPage);
        storyPage.setStoryChapter(this);
    }
}
