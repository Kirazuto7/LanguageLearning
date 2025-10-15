package com.example.language_learning.ai.dtos.moderation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ModerationResponse(
    List<Result> results
) {}
