package com.example.language_learning.shared.dtos.logging;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClientLogDTO(
        String level,
        String message,
        Object context
) {}