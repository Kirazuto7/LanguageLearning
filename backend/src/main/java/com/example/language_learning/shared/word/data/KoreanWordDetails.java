package com.example.language_learning.shared.word.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record KoreanWordDetails(
        @JsonProperty("hangul") String hangul,
        @JsonProperty("hanja") String hanja, // Optional
        @JsonProperty("romaja") String romaja
) implements WordDetails {}