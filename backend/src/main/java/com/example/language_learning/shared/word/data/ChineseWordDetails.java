package com.example.language_learning.shared.word.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChineseWordDetails(
        @JsonProperty("simplified") String simplified,
        @JsonProperty("traditional") String traditional,
        @JsonProperty("pinyin") String pinyin,
        @JsonProperty("toneNumber") String toneNumber
) implements WordDetails {}