package com.example.language_learning.dto.api.details;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIThaiVocabularyItemDTO(
        @JsonProperty("englishTranslation") String englishTranslation,
        @JsonProperty("thaiScript") String thaiScript,
        @JsonProperty("romanization") String romanization,
        @JsonProperty("tonePattern") String tonePattern
) {}