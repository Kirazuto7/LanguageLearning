package com.example.language_learning.requests;

import lombok.Builder;

@Builder
public record TtsRequest(
    String text,
    String voiceId,
    String language
) {}
