package com.example.language_learning.shared.services;

import com.example.language_learning.shared.exceptions.ImageDownloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final StorageProvider storageProvider;

    public String saveImageFromUrl(String imageUrl) {
        byte[] imageData = downloadImage(imageUrl);
        String fileName = generateUniqueFileName(imageUrl);
        return storageProvider.save(imageData, fileName);
    }

    private byte[] downloadImage(String imageUrl) {
        URL url = null;
        try {
            url = URI.create(imageUrl).toURL();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try (
                InputStream in = url.openStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        }
        catch (IOException e) {
            log.error("Failed to download image from url: {}", imageUrl, e);
            throw new ImageDownloadException("Failed to download image from URL: " + imageUrl, e);
        }
    }

    private String generateUniqueFileName(String imageUrl) {
        String extension = ".png";
        int lastDot = imageUrl.lastIndexOf('.');
        if (lastDot > 0 && imageUrl.lastIndexOf('/') < lastDot) {
            extension = imageUrl.substring(lastDot);
        }
        return UUID.randomUUID().toString() + extension;
    }

}
