package com.example.language_learning.config;

import com.example.language_learning.config.properties.MinioProperties;
import com.example.language_learning.config.properties.StorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class StorageConfig {

    @Bean
    public S3Client s3Client(StorageProperties properties) {
        return S3Client.builder()
                .endpointOverride(URI.create(properties.url()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.accessKey(), properties.secretKey())
                ))
                .region(Region.US_EAST_1)
                .forcePathStyle(true)
                .build();
    }
}
