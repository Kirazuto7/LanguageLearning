package com.example.language_learning.shared.exceptions;

public class SyncWorkflowException extends RuntimeException {
    public SyncWorkflowException(String message) {
        super(message);
    }

    public SyncWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
