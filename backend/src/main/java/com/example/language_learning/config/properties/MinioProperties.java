package com.example.language_learning.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

/**
 * Configuration properties for connecting to the MinIO server.
 * This record is bound to the properties prefixed with "minio" in the application configuration.
 */
@ConfigurationProperties(prefix = "minio")
@Profile("!prod")
public record MinioProperties(
   String url,
   String accessKey,
   String secretKey,
   String bucket,
   String publicUrl
) implements StorageProperties {}
