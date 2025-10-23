package com.example.language_learning.ai.dtos.moderation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Categories(
        boolean sexual,
        @JsonProperty("sexual/minors") boolean sexualMinors,
        boolean hate,
        @JsonProperty("hate/threatening") boolean hateThreatening,
        @JsonProperty("harassment") boolean harassment,
        @JsonProperty("harassment/threatening") boolean harassmentThreatening,
        @JsonProperty("self-harm") boolean selfHarm,
        @JsonProperty("self-harm/intent") boolean selfHarmIntent,
        @JsonProperty("self-harm/instructions") boolean selfHarmInstructions,
        boolean violence,
        @JsonProperty("violence/graphic") boolean violenceGraphic,
        boolean illicit,
        @JsonProperty("illicit/violent") boolean illicitViolent
) {}