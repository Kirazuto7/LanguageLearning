package com.example.language_learning.shared.data;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseChapter extends BaseEntity {
    @Column(nullable = false)
    protected String title;

    @Column(nullable = false)
    protected String nativeTitle;
}
