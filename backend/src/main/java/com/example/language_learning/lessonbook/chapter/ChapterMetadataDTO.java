package com.example.language_learning.lessonbook.chapter;

import lombok.Builder;

@Builder
public record ChapterMetadataDTO (
    String title,
    String nativeTitle,
    String topic
) {}
