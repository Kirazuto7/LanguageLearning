package com.example.language_learning.ai.dtos.moderation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Result(
    boolean flagged,
    Categories categories
) {}
