package com.example.language_learning.dto.languages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnglishWordDTO extends WordDTO {
    private String word;
    @Override
    public String getPrimaryRepresentation() {
        if (word != null && !word.trim().isEmpty()) {
            return getWord();
        }
        return "";
    }
}