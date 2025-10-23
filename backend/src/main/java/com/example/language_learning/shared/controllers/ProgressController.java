package com.example.language_learning.shared.controllers;

import com.example.language_learning.shared.dtos.progress.ProgressUpdateDTO;
import com.example.language_learning.shared.services.ProgressService;
import com.example.language_learning.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;

    @GetMapping("/tasks/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgressUpdateDTO> getTaskStatus(@PathVariable String id, @AuthenticationPrincipal User user) {
        var cachedUpdate = progressService.getLatestUpdate(id);
        if (cachedUpdate == null) {
            return ResponseEntity.notFound().build();
        }

        if (!cachedUpdate.userId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(cachedUpdate.update());
    }
}
