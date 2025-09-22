package com.example.language_learning.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for connecting to the MinIO server.
 * This record is bound to the properties prefixed with "minio" in the application configuration.
 */
@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
   String url,
   String accessKey,
   String secretKey,
   String bucket
) {}
