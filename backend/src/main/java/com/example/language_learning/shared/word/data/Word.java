package com.example.language_learning.shared.word.data;

import com.example.language_learning.shared.data.BaseEntity;
import com.example.language_learning.shared.utils.JpaMapConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "words")
public class Word extends BaseEntity  {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String englishTranslation; // English translation

    @Column(nullable = false)
    private String language;

    @Convert(converter = JpaMapConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private WordDetails details;
}
