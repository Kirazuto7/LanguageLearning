package com.example.language_learning.shared.exceptions;

public class AIEngineException extends RuntimeException {
    public AIEngineException(String message) {
        super(message);
    }

    public AIEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
