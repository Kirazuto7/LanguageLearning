package com.example.language_learning.entity.models.details;

import com.example.language_learning.entity.models.WordDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChineseWordDetails(
        @JsonProperty("simplified") String simplified,
        @JsonProperty("traditional") String traditional,
        @JsonProperty("pinyin") String pinyin,
        @JsonProperty("toneNumber") String toneNumber
) implements WordDetails {}