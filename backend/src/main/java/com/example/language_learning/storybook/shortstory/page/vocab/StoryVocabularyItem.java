package com.example.language_learning.storybook.shortstory.page.vocab;

import com.example.language_learning.shared.data.BaseEntity;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "story_vocabulary_items")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoryVocabularyItem extends BaseEntity {

    @Column(nullable = false)
    private String word;

    private String translation;

    @Column(nullable = false)
    private String stem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_page_id")
    @JsonBackReference
    private StoryPage storyPage;
}
