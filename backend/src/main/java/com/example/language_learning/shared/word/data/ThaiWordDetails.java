package com.example.language_learning.shared.word.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ThaiWordDetails(
        @JsonProperty("thaiScript") String thaiScript,
        @JsonProperty("romanization") String romanization,
        @JsonProperty("tonePattern") String tonePattern
) implements WordDetails {}