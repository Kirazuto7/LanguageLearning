package com.example.language_learning.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIProofreadResponse(
    @JsonProperty("correctedSentence") String correctedSentence,
    @JsonProperty("feedback") String feedback
) {}
