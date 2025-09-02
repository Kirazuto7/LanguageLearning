package com.example.language_learning.controllers;

import com.example.language_learning.requests.TtsRequest;
import com.example.language_learning.services.TtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
public class TtsController {

    private final TtsService ttsService;

    @PostMapping("/speak")
    public Mono<ResponseEntity<byte[]>> speak(@RequestBody TtsRequest request) {
        return ttsService.getSpeechAudio(request)
                .map(audioBytes -> ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(audioBytes));
    }
}
