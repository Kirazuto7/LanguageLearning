package com.example.language_learning.storybook.chapter.page;

import com.example.language_learning.shared.word.data.Word;
import com.example.language_learning.shared.data.BasePage;
import com.example.language_learning.storybook.chapter.StoryChapter;
import com.example.language_learning.storybook.chapter.page.paragraph.StoryParagraph;
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
    @JoinColumn(name = "story_chapter_id")
    @JsonBackReference
    private StoryChapter storyChapter;

    public void addParagraph(StoryParagraph paragraph) {
        paragraphs.add(paragraph);
        paragraph.setStoryPage(this);
    }

    public void addVocabulary(Word word) {
        vocabulary.add(word);
    }
}
