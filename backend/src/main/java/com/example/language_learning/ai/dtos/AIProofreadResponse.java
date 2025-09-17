package com.example.language_learning.ai.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIProofreadResponse(
    @JsonProperty("correctedSentence") String correctedSentence,
    @JsonProperty("feedback") String feedback
) {}
