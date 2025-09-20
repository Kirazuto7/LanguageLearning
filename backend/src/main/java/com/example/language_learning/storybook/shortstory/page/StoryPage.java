package com.example.language_learning.storybook.shortstory.page;

import com.example.language_learning.shared.word.data.Word;
import com.example.language_learning.shared.data.BasePage;
import com.example.language_learning.storybook.shortstory.ShortStory;
import com.example.language_learning.storybook.shortstory.page.paragraph.StoryParagraph;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "story_pages")
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StoryPage extends BasePage {

    @Column(columnDefinition = "TEXT")
    private String englishSummary;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "storyPage")
    @OrderBy("paragraphNumber ASC")
    @Builder.Default
    @BatchSize(size = 10)
    private List<StoryParagraph> paragraphs = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "story_page_vocabulary",
            joinColumns = @JoinColumn(name = "story_page_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    @Builder.Default
    @BatchSize(size = 10)
    private List<Word> vocabulary = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_story_id")
    @JsonBackReference
    private ShortStory shortStory;

    public void addParagraph(StoryParagraph paragraph) {
        paragraphs.add(paragraph);
        paragraph.setStoryPage(this);
    }

    public void addVocabulary(Word word) {
        vocabulary.add(word);
    }
}
