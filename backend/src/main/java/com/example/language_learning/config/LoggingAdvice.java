package com.example.language_learning.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class LoggingAdvice {
    @ModelAttribute
    public void logRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.equals("/api/users/health") || path.startsWith("/api/progress//tasks/")) {
            return;
        }

        Object errorRequestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (errorRequestUri != null) {
            log.warn("Request to '{}' failed, forwarded to the error page.", errorRequestUri);
        }
        else {
            log.info("Incoming request path: {}", path);
        }
    }
}