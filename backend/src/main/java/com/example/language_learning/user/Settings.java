package com.example.language_learning.user;

import com.example.language_learning.shared.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "settings")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Settings extends BaseEntity {
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
