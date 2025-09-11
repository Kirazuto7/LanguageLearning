package com.example.language_learning.entity.models.details;

import com.example.language_learning.entity.models.WordDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ThaiWordDetails(
        @JsonProperty("thaiScript") String thaiScript,
        @JsonProperty("romanization") String romanization,
        @JsonProperty("tonePattern") String tonePattern
) implements WordDetails {}