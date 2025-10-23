package com.example.language_learning.shared.services;

import com.example.language_learning.shared.exceptions.ImageDownloadException;
import com.example.language_learning.shared.exceptions.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final StorageProvider storageProvider;

    // --- Configuration for Image Resizing ---
    private static final int TARGET_WIDTH = 1024;
    private static final int TARGET_HEIGHT = 1024;


    public String saveImageFromBase64(String base64ImageData) {
        // The base64 string might include a data URI prefix (e.g., "data:image/png;base64,").
        // We need to strip this prefix before decoding.
        String pureBase64 = base64ImageData;
        int commaIndex = base64ImageData.indexOf(',');
        if (commaIndex != -1) {
            pureBase64 = base64ImageData.substring(commaIndex + 1);
        }
        byte[] imageData = Base64.getDecoder().decode(pureBase64);
        byte[] resizedImageData = resizeImageData(imageData);
        // The Stable Diffusion API defaults to PNG.
        String fileName = generateUniqueFileName(".png");
        String fileUrl = storageProvider.save(resizedImageData, fileName);
        log.info("Successfully uploaded image {} to storage.", fileName);
        return fileUrl;
    }

    public String saveImageFromUrl(String imageUrl) {
        byte[] imageData = downloadImage(imageUrl);
        byte[] resizedImageData = resizeImageData(imageData);
        String extension = extractExtensionFromUrl(imageUrl);
        String fileName = generateUniqueFileName(extension);
        String fileUrl = storageProvider.save(resizedImageData, fileName);
        log.info("Successfully uploaded image {} from URL {} to storage.", fileName, imageUrl);
        return fileUrl;
    }

    private byte[] resizeImageData(byte[] originalImageData) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(originalImageData);
            BufferedImage originalImage = ImageIO.read(bis);

            if (originalImage == null) {
                log.warn("Could not decode image data; skipping resize.");
                return originalImageData;
            }

            BufferedImage resizedImage = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.drawImage(originalImage, 0, 0, TARGET_WIDTH, TARGET_HEIGHT, null);
            graphics.dispose();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", bos);
            return bos.toByteArray();
        }
        catch (IOException e) {
            log.error("Failed to resize image", e);
            throw new ImageProcessingException("Failed to resize image", e);
        }
    }

    private byte[] downloadImage(String imageUrl) {
        URL url;
        try {
            url = URI.create(imageUrl).toURL();
        }
        catch (MalformedURLException e) {
            // Wrapping in a less specific exception for simplicity, but a custom one could be used.
            throw new RuntimeException("Invalid image URL: " + imageUrl, e);
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

    private String extractExtensionFromUrl(String imageUrl) {
        int lastDot = imageUrl.lastIndexOf('.');
        if (lastDot > 0 && imageUrl.lastIndexOf('/') < lastDot) {
            return imageUrl.substring(lastDot);
        }
        // Default to .png if no extension is found
        return ".png";
    }

    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + extension;
    }

}
