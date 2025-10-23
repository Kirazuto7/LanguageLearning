package com.example.language_learning.storybook.shortstory.page.paragraph;

import com.example.language_learning.shared.data.BaseEntity;
import com.example.language_learning.storybook.shortstory.page.StoryPage;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

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

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "words_to_highlight", nullable = false, columnDefinition = "jsonb")
    private Set<String> wordsToHighlight = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_page_id")
    @JsonBackReference
    private StoryPage storyPage;
}
