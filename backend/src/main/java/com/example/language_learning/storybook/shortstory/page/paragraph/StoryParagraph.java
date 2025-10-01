package com.example.language_learning.storybook.shortstory.page.paragraph;

import com.example.language_learning.shared.data.BaseEntity;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "story_paragraphs")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoryParagraph extends BaseEntity {
    private int paragraphNumber;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_page_id")
    @JsonBackReference
    private StoryPage storyPage;
}
