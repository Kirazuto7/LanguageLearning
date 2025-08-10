package com.example.language_learning.entity.lessons;

import com.example.language_learning.entity.models.Page;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String title;

    @OneToOne
    @JoinColumn(name = "page_id")
    private Page page;
}
