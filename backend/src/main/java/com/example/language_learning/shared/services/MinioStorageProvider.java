package com.example.language_learning.shared.services;

import com.example.language_learning.config.properties.MinioProperties;
import com.example.language_learning.config.properties.StorageProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URL;

/**
 * An implementation of the StorageProvider interface that uses a MinIO server.
 * This class is configured to work with any S3-compatible object storage.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MinioStorageProvider implements StorageProvider {

    private final S3Client s3Client;
    //private final StorageProperties storageProperties;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(minioProperties.bucket()).build());
            log.info("MinIO bucket '{}' already exists.", minioProperties.bucket());
        }
        catch (NoSuchBucketException e) {
            log.info("MinIO bucket '{}' not found. Creating it...", minioProperties.bucket());
            s3Client.createBucket(CreateBucketRequest.builder().bucket(minioProperties.bucket()).build());
            log.info("MinIO bucket '{}' created successfully.", minioProperties.bucket());
        }
    }

    @Override
    public String save(byte[] fileData, String fileName) {
        // 1. Create a PutObjectRequest with the bucket name, key (fileName), and metadata.
        String bucketName = minioProperties.bucket();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                .bucket(bucketName)
                                                .key(fileName)
                                                .build();
        // 2. Call s3Client.putObject(request, requestBody).
        try {
            log.info("MinioStorageProvider uploading file {} ...", fileName);
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
            log.info("MinioStorageProvider successfully uploaded file '{}' to bucket: '{}'.", fileName, bucketName);
        }
        catch (Exception e) {
            log.error("Failed to upload file {} to bucket {}: {}", fileName, bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to save file to storage.", e);
        }

        // 3. Construct and return the public URL of the newly uploaded object.
        // Use S3Client's utility to build the encoded URL
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        String internalUrl = s3Client.utilities().getUrl(getUrlRequest).toString();

        // Replace the internal Docker network URL with the public-facing URL.
        return internalUrl.replace(minioProperties.url(), minioProperties.publicUrl());
    }
}
