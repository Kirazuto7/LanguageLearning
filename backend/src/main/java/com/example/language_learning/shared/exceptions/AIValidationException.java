package com.example.language_learning.shared.exceptions;

import com.networknt.schema.ValidationMessage;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class AIValidationException extends RuntimeException {
    private final Set<ValidationMessage> validationMessages;

    public AIValidationException(String message, Set<ValidationMessage> validationMessages) {
        super(buildErrorMessage(message, validationMessages));
        this.validationMessages = validationMessages;
    }

    private static String buildErrorMessage(String message, Set<ValidationMessage> validationMessages) {
        if (validationMessages == null || validationMessages.isEmpty()) {
            return message;
        }
        String validationDetails = validationMessages.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.joining(", "));
        return message + " Validation errors: [" + validationDetails + "]";
    }
}
