package com.example.language_learning.entity.models.details;

import com.example.language_learning.entity.models.WordDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SpanishWordDetails(
        @JsonProperty("lemma") String lemma,
        @JsonProperty("gender") String gender,
        @JsonProperty("pluralForm") String pluralForm
) implements WordDetails {}