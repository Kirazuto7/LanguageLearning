package com.example.language_learning.entity.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GenericWordDetails(
    @JsonProperty("nativeWord") String nativeWord,
    @JsonProperty("phoneticSpelling") String phoneticSpelling
) implements WordDetails {}
