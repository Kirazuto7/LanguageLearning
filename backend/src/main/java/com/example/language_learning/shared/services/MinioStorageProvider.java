package com.example.language_learning.shared.services;

import com.example.language_learning.config.properties.MinioProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;


/**
 * An implementation of the StorageProvider interface that uses a MinIO server.
 * This class is configured to work with any S3-compatible object storage.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Profile("!test") // Exclude this bean from the 'test' profile
public class MinioStorageProvider implements StorageProvider {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;
    private final S3Client s3Client;
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
        setPublicReadPolicy();
    }

    @Override
    public String save(byte[] fileData, String fileName) {
        // 1. Create a PutObjectRequest with the bucket name, key (fileName), public read access, and metadata.
        String bucketName = minioProperties.bucket();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                .bucket(bucketName)
                                                .key(fileName)
                                                .acl(ObjectCannedACL.PUBLIC_READ)
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

        // 3. Construct and return the public URL of the newly uploaded object. Format: PROTOCOL://HOSTNAME:PORT/BUCKET_NAME/OBJECT_KEY
        String publicUrl = minioProperties.publicUrl();
        if (publicUrl.endsWith("/")) {
            publicUrl = publicUrl.substring(0, publicUrl.length() - 1);
        }
        return String.format("%s/%s/%s", publicUrl, bucketName, fileName);
    }

    @Override
    public void remove(String fileName) {
        String bucketName = minioProperties.bucket();
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        try {
            log.info("MinioStorageProvider deleting file {} ...", fileName);
            s3Client.deleteObject(deleteObjectRequest);
            log.info("MinioStorageProvider successfully deleted file '{}' from bucket: '{}'.", fileName, bucketName);
        }
        catch (Exception e) {
            log.error("Failed to delete file {} from bucket {}: {}", fileName, bucketName, e.getMessage(), e);
        }
    }

    private void setPublicReadPolicy() {
        String bucketName = minioProperties.bucket();
        try {
            log.info("Applying public-read policy to bucket: {}", bucketName);
            String policy = """
            {
             "Version": "2012-10-17",
             "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": "*",
                    "Action": ["s3:GetObject"],
                    "Resource": ["arn:aws:s3:::%s/*"]
                }
             ]
            }        
            """.formatted(bucketName);
            PutBucketPolicyRequest policyRequest = PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(policy)
                    .build();
            s3Client.putBucketPolicy(policyRequest);
            log.info("Successfully applied public-read policy to bucket: {}", bucketName);
        }
        catch (Exception e) {
            log.error("Failed to apply public-read policy to bucket {}: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to set bucket policy.", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (!List.of("create", "create-drop").contains(ddlAuto)) {
            log.info("Skipping MinIO bucket cleanup because ddl-auto is not 'destructive' (current value: {}).", ddlAuto);
            return;
        }

        String bucketName = minioProperties.bucket();
        log.info("Performing cleanup for MinIO bucket: {}", bucketName);

        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();
            List<S3Object> objects = s3Client.listObjectsV2(listObjectsRequest).contents();

            if (objects.isEmpty()) {
                log.info("Bucket {} is already empty. No cleanup needed.", bucketName);
                return;
            }

            List<ObjectIdentifier> toDelete = new ArrayList<>();
            for (S3Object object : objects) {
                toDelete.add(ObjectIdentifier.builder().key(object.key()).build());
            }

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(toDelete).build())
                    .build();
            s3Client.deleteObjects(deleteObjectsRequest);
            log.info("Successfully deleted all objects from bucket: {}", bucketName);
        }
        catch (Exception e) {
            log.error("Failed to clean up bucket {}: {}", bucketName, e.getMessage(), e);
        }
    }
}
