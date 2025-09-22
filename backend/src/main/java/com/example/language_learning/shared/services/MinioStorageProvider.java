package com.example.language_learning.shared.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * An implementation of the StorageProvider interface that uses a MinIO server.
 * This class is configured to work with any S3-compatible object storage.
 */
@Service
@Slf4j
public class MinioStorageProvider implements StorageProvider {
    @Override
    public String save(byte[] fileData, String fileName) {
        // TODO: Implement MinIO / S3 storage logic here
        // 1. Create a PutObjectRequest with the bucket name, key (fileName), and metadata.
        // 2. Create a RequestBody from the fileData byte array.
        // 3. Call s3Client.putObject(request, requestBody).
        // 4. Construct and return the public URL of the newly uploaded object.
        log.warn("MinioStorageProvider.save not yet implemented. Returning a dummy URL.");
        return "https://example.com/dummy-image/" + fileName;
    }
}
