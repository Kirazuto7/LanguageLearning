package com.example.language_learning.dto.models;

import lombok.Builder;

@Builder
public record ConjugationExampleDTO(
        Long id,
        String infinitive,
        String conjugatedForm,
        String exampleSentence,
        String sentenceTranslation
) {}
