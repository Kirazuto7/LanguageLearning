package com.example.language_learning.entity.models;

import com.example.language_learning.mapper.util.JpaMapConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(columnDefinition = "TEXT")
    private String englishTranslation; // English translation

    @Column(nullable = false)
    private String language;

    @Convert(converter = JpaMapConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private WordDetails details;
}
