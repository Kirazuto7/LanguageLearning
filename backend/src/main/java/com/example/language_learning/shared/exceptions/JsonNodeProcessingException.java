package com.example.language_learning.shared.exceptions;

public class JsonNodeProcessingException extends RuntimeException {
    public JsonNodeProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}