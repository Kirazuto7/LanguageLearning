package com.example.language_learning.entity.languages;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "korean_words")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KoreanWord extends Word {
    private String hangeul;
    @Column(nullable = true)
    private String hanja;
    @Column(nullable = true)
    private String romaja;
}
