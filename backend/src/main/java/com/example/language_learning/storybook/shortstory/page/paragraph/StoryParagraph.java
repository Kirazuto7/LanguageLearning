package com.example.language_learning.storybook.shortstory.page.paragraph;

import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "story_paragraphs")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryParagraph {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int paragraphNumber;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_page_id")
    @JsonBackReference
    private StoryPage storyPage;
}
