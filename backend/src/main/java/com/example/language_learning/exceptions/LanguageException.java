package com.example.language_learning.exceptions;

public class LanguageException extends RuntimeException {
    public LanguageException(String message) {
        super(message);
    }
    public LanguageException(String message, Throwable cause) {
        super(message, cause);
    }
}
