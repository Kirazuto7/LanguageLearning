package com.example.language_learning.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Manually configures the ChatClient beans for each AI model.
 * This provides full control and avoids auto-configuration issues.
 */
@Configuration
public class AIChatClientConfig {

    // --- Properties for Ollama Instance 1 (qwen3) ---
    @Value("${spring.ai.ollama1.base-url}")
    private String ollama1BaseUrl;

    @Value("${spring.ai.ollama1.chat.model}")
    private String ollama1ModelName;

    @Value("${spring.ai.ollama1.chat.options.format:json}")
    private String ollama1OptionsFormat;

    @Value("${spring.ai.ollama1.chat.options.num-ctx:8192}")
    private Integer ollama1OptionsNumCtx;

    @Value("${spring.ai.ollama1.chat.options.num-predict:8192}")
    private Integer ollama1OptionsNumPredict;

    @Value("${spring.ai.ollama1.chat.options.temperature:0.5}")
    private Double ollama1OptionsTemperature;

    // --- Properties for Ollama Instance 2 (exaone) ---
    @Value("${spring.ai.ollama2.base-url}")
    private String ollama2BaseUrl;

    @Value("${spring.ai.ollama2.chat.model}")
    private String ollama2ModelName;

    @Value("${spring.ai.ollama2.chat.options.format:json}")
    private String ollama2OptionsFormat;

    @Value("${spring.ai.ollama2.chat.options.num-ctx:8192}")
    private Integer ollama2OptionsNumCtx;

    @Value("${spring.ai.ollama2.chat.options.num-predict:8192}")
    private Integer ollama2OptionsNumPredict;

    @Value("${spring.ai.ollama2.chat.options.temperature:0.5}")
    private Double ollama2OptionsTemperature;

    // 1. Create separate OllamaApi beans for each instance using the builder
    @Bean
    public OllamaApi ollamaApi1() {
        return OllamaApi.builder().baseUrl(ollama1BaseUrl).build();
    }

    @Bean
    public OllamaApi ollamaApi2() {
        return OllamaApi.builder().baseUrl(ollama2BaseUrl).build();
    }

    // 2. Create OllamaChatModel beans using the builder pattern
    @Bean
    public OllamaChatModel qwen3ChatModel(OllamaApi ollamaApi1) {
        OllamaOptions options = OllamaOptions.builder()
                .model(ollama1ModelName)
                .format(ollama1OptionsFormat)
                .numCtx(ollama1OptionsNumCtx)
                .numPredict(ollama1OptionsNumPredict)
                .temperature(ollama1OptionsTemperature)
                .build();

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi1)
                .defaultOptions(options)
                .build();
    }

    @Bean
    public OllamaChatModel exaoneChatModel(OllamaApi ollamaApi2) {
        OllamaOptions options = OllamaOptions.builder()
                .model(ollama2ModelName)
                .format(ollama2OptionsFormat)
                .numCtx(ollama2OptionsNumCtx)
                .numPredict(ollama2OptionsNumPredict)
                .temperature(ollama2OptionsTemperature)
                .build();

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi2)
                .defaultOptions(options)
                .build();
    }

    // 3. Create ChatClient beans based on the specific ChatModels
    @Bean("qwen3")
    public ChatClient qwen3ChatClient(OllamaChatModel qwen3ChatModel) {
        return ChatClient.builder(qwen3ChatModel).build();
    }

    @Bean("exaone")
    public ChatClient exaoneChatClient(OllamaChatModel exaoneChatModel) {
        return ChatClient.builder(exaoneChatModel).build();
    }

}
