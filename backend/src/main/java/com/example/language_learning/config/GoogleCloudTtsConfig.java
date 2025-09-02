package com.example.language_learning.config;

import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GoogleCloudTtsConfig {

    @Bean
    public TextToSpeechClient textToSpeechClient() {
        try {
            return TextToSpeechClient.create();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create Google Cloud TextToSpeechClient. Please check your authentication credentials.", e);
        }
    }
}
