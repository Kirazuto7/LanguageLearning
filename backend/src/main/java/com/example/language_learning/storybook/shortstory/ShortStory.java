package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.shared.data.BaseChapter;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.StoryBook;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "short_stories")
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShortStory extends BaseChapter {

    private String genre;
    private String topic;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "shortStory")
    @OrderBy("pageNumber ASC")
    @Builder.Default
    private List<StoryPage> storyPages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "story_book_id")
    private StoryBook storyBook;

    public void addStoryPage(StoryPage storyPage) {
        storyPages.add(storyPage);
        storyPage.setShortStory(this);
    }
}
