package com.example.language_learning.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settings")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language;
    private String difficulty;

    @Column(name = "theme", nullable = false, columnDefinition = "varchar(255) default 'default'")
    private String theme;
}
