package com.example.language_learning.shared.exceptions;

public class PageGenerationException extends RuntimeException {
    public PageGenerationException(String message) {
        super(message);
    }

    public PageGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
