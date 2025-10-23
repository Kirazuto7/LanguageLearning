package com.example.language_learning.storybook.shortstory;

import com.example.language_learning.shared.data.BaseChapter;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.example.language_learning.storybook.StoryBook;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "short_stories")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShortStory extends BaseChapter {

    private String genre;
    private String topic;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "shortStory")
    @OrderBy("id ASC")
    @Builder.Default
    @BatchSize(size = 10)
    private List<StoryPage> storyPages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "story_book_id")
    private StoryBook storyBook;

    public void addStoryPage(StoryPage storyPage) {
        storyPages.add(storyPage);
        storyPage.setShortStory(this);
    }
}
