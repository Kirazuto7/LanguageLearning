package com.example.language_learning.dto.models;

import lombok.Builder;

@Builder
public record ChapterMetadataDTO (
    String title,
    String nativeTitle,
    String topic
) {}
