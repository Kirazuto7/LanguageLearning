package com.example.language_learning.services;

import com.example.language_learning.config.GoogleCloudTtsConfig;
import com.example.language_learning.requests.TtsRequest;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TtsService {
    private final TextToSpeechClient textToSpeechClient;
    private static final String DEFAULT_VOICE = "ko-KR-Wavenet-A";

    private static final Map<String, String> voiceMap = Map.of(
        "jinny", DEFAULT_VOICE,  // Korean, Female
            "sakura", "ja-JP-Wavenet-A", // Japanese, Female
            "riku", "ja-JP-Wavenet-D",   // Japanese, Male
            "yuna", "ko-KR-Wavenet-A",   // Korean, Female
            "jinwoo", "ko-KR-Wavenet-D"  // Korean, Male
    );


    private static final Map<String, String> langMap = Map.of(
            "English", "en-US",
            "Japanese", "ja-JP",
            "Korean", "ko-KR",
            "Chinese", "cmn-CN",
            "Thai", "th-TH",
            "Italian", "it-IT",
            "French", "fr-FR",
            "Spanish", "es-ES",
            "German", "de-DE"
    );


    public Mono<byte[]> getSpeechAudio(TtsRequest request) {
        return Mono.fromCallable(() -> {
            String langCode = langMap.getOrDefault(request.language(), "en-US");
            String voiceName = voiceMap.getOrDefault(request.voiceId(), DEFAULT_VOICE);

            SynthesisInput input = SynthesisInput.newBuilder().setText(request.text()).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(langCode)
                    .setName(voiceName)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            log.info("Response: {}", response);
            ByteString audioContents = response.getAudioContent();

            return audioContents.toByteArray();
        });
    }

}
