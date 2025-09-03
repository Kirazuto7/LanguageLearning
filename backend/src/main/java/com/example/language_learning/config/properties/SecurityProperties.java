package com.example.language_learning.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

import java.util.List;

@ConfigurationProperties(prefix = "application.security")
public record SecurityProperties (
    @Name("public-paths") List<String> publicPaths
) {}
