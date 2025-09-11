package com.example.language_learning.entity.models.details;

import com.example.language_learning.entity.models.WordDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record KoreanWordDetails(
        @JsonProperty("hangul") String hangul,
        @JsonProperty("hanja") String hanja, // Optional
        @JsonProperty("romaja") String romaja
) implements WordDetails {}