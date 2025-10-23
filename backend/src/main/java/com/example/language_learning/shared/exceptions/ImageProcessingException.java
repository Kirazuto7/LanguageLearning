package com.example.language_learning.shared.exceptions;

public class ImageProcessingException extends RuntimeException {
    public ImageProcessingException(String message) {
        super(message);
    }
    public ImageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
