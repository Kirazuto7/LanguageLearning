package com.example.language_learning.shared.exceptions;

public class ImageDownloadException extends RuntimeException {
    public ImageDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
