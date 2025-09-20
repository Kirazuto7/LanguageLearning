package com.example.language_learning.shared.word.data;

import com.example.language_learning.shared.utils.JpaMapConverter;
import com.example.language_learning.storybook.chapter.page.StoryPage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String englishTranslation; // English translation

    @Column(nullable = false)
    private String language;

    @Convert(converter = JpaMapConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private WordDetails details;

    @ManyToMany(mappedBy =  "vocabulary")
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private List<StoryPage> storyPages = new ArrayList<>();
}
