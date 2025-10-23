package com.example.language_learning.shared.word.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ItalianWordDetails(
        @JsonProperty("lemma") String lemma,
        @JsonProperty("gender") String gender,
        @JsonProperty("pluralForm") String pluralForm
) implements WordDetails {}