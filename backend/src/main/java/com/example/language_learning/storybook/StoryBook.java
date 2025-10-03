package com.example.language_learning.storybook;

import com.example.language_learning.shared.data.BaseBook;
import com.example.language_learning.storybook.shortstory.ShortStory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
    name = "StoryBook.withShortStories",
    attributeNodes = {
        @NamedAttributeNode(value = "shortStories")
    }
)

@Entity
@Table(name = "story_books")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StoryBook extends BaseBook {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "storyBook", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    @Builder.Default
    private List<ShortStory> shortStories = new ArrayList<>();

    public void addShortStory(ShortStory shortStory) {
        shortStories.add(shortStory);
        shortStory.setStoryBook(this);
    }
}
