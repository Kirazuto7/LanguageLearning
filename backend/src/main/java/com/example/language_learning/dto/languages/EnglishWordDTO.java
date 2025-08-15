package com.example.language_learning.dto.languages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnglishWordDTO extends WordDTO {
    // No extra fields are needed for English, as the 'translation' field in the base class holds the word.
}