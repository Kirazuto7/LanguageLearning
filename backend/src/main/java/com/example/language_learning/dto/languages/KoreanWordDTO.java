package com.example.language_learning.dto.languages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KoreanWordDTO extends WordDTO{
    private String hangeul;
    private String hanja;

    @Override
    public String getPrimaryRepresentation() {
        if (hangeul != null && !hangeul.trim().isEmpty()) {
            return getHangeul();
        }
        if (hanja != null && !hanja.trim().isEmpty()) {
            return getHanja();
        }
        return "";
    }
}
