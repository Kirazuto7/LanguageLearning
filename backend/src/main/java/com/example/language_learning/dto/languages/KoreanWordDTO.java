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
}
