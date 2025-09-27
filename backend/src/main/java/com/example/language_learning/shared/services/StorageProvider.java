package com.example.language_learning.shared.services;

/**
 * An interface that defines the contract for a file storage provider.
 * This allows the application to be decoupled from the specific storage technology (e.g., MinIO, AWS S3).
 */
public interface StorageProvider {
    /**
     * Saves the given file data to the storage provider.
     *
     * @param fileData The raw byte array of the file to save.
     * @param fileName The desired name for the file in the storage.
     * @return The public URL of the saved file.
     */
    String save(byte[] fileData, String fileName);

    /**
     * Removes the specified file from the storage provider.
     *
     * @param fileName The name of the file to remove.
     */
     void remove(String fileName);
}
