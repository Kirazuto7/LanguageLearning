package com.example.language_learning.user;

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

    @Column(name = "theme", nullable = false)
    @Builder.Default
    private String theme = "default";

    @Column(name = "mascot", nullable = false)
    @Builder.Default
    private String mascot = "jinny";

    @Column(name ="auto_speak_enabled", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private boolean autoSpeakEnabled = true;
}
