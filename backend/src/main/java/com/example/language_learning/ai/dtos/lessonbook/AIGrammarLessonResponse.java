package com.example.language_learning.ai.dtos.lessonbook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A dedicated DTO to represent the exact JSON structure for a grammar lesson
 * as returned by the AI. This acts as an Anti-Corruption Layer, decoupling the
 * application's internal DTOs from the external API's contract.
 * Using a record here provides a concise, immutable data carrier.
 */
public record AIGrammarLessonResponse(
    @JsonProperty("title") String title,
    @JsonProperty("grammarConcept") String grammarConcept,
    @JsonProperty("explanation") String explanation,
    @JsonProperty("exampleSentences") List<AISentenceDTO> exampleSentences
) { }