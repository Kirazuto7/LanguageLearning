package com.example.language_learning.storybook.shortstory.page.vocab;

import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "story_vocabulary_items")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryVocabularyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    private String translation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_page_id")
    @JsonBackReference
    private StoryPage storyPage;
}
