package com.example.language_learning.config;

import com.example.language_learning.config.properties.MinioProperties;
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
public class MinioConfig {

    @Bean
    public S3Client s3Client(MinioProperties minioProperties) {
        return S3Client.builder()
                .endpointOverride(URI.create(minioProperties.url()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minioProperties.accessKey(), minioProperties.secretKey())
                ))
                .region(Region.US_EAST_1)
                .forcePathStyle(true)
                .build();
    }
}
