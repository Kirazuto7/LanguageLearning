package com.example.language_learning.shared.controllers;

import com.example.language_learning.shared.dtos.logging.ClientLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@Slf4j
public class ClientLoggingController {

    @PostMapping("/client")
    public ResponseEntity<Void> logClientMessage(@RequestBody ClientLogDTO clientLog) {
        String logMessage = String.format("%s %s", clientLog.message(), clientLog.context() != null ? "{}" : "");
        switch (clientLog.level().toUpperCase()) {
            case "INFO" -> log.info(logMessage, clientLog.context());
            case "DEBUG" -> log.debug(logMessage, clientLog.context());
            case "WARN" -> log.warn(logMessage, clientLog.context());
            case "ERROR" -> log.error(logMessage, clientLog.context());
            default -> log.trace(logMessage, clientLog.context());
        }
        return ResponseEntity.ok().build();
    }
}