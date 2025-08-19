package com.example.language_learning.dto.models;

import lombok.Builder;

@Builder
public record ConjugationExampleDTO(
        Long id,
        String tense,
        String conjugatedForm,
        String exampleSentence,
        String sentenceTranslation
) {}
