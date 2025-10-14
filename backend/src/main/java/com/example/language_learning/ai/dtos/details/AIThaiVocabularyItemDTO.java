package com.example.language_learning.ai.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIThaiVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("thaiScript") String thaiScript,
        @JsonProperty("romanization") String romanization,
        @JsonProperty("tonePattern") String tonePattern
) {}