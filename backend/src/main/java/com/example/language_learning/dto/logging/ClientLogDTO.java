package com.example.language_learning.dto.logging;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClientLogDTO(
        String level,
        String message,
        Object context
) {}