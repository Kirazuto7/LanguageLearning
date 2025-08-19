package com.example.language_learning.dto.models;

import java.util.List;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChapterDTO (
     Long id,
     int chapterNumber,
     String title,
     String nativeTitle,
     List<PageDTO> pages
) {}