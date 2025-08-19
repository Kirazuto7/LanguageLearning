package com.example.language_learning.config;

import com.example.language_learning.config.properties.KoreanAIProperties;
import com.example.language_learning.config.properties.DefaultAIProperties;
import com.example.language_learning.config.properties.JapaneseAIProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Manually configures the ChatClient beans for each AI model.
 * This provides full control and avoids auto-configuration issues.
 */
@Configuration
@EnableConfigurationProperties({DefaultAIProperties.class, KoreanAIProperties.class, JapaneseAIProperties.class})
public class AIChatClientConfig {

    @Value("classpath:prompts/default_system_prompt.txt")
    private Resource systemPrompt;

    @Value("classpath:prompts/japanese_system_prompt.txt")
    private Resource japaneseSystemPrompt;

    // 1. Create separate OllamaApi beans for each instance using the builder
    @Bean
    public OllamaApi ollamaApi1(DefaultAIProperties props) {
        return OllamaApi.builder().baseUrl(props.baseUrl()).build();
    }

    @Bean
    public OllamaApi ollamaApi2(KoreanAIProperties props) {
        return OllamaApi.builder().baseUrl(props.baseUrl()).build();
    }

    @Bean
    public OllamaApi ollamaApi3(JapaneseAIProperties props) {
        return OllamaApi.builder().baseUrl(props.baseUrl()).build();
    }

    // 2. Create OllamaChatModel beans using the builder pattern
    @Bean
    public OllamaChatModel defaultChatModel(OllamaApi ollamaApi1, DefaultAIProperties props) {
        OllamaOptions options = OllamaOptions.builder()
                .model(props.chat().model())
                .format(props.chat().options().format())
                .numCtx(props.chat().options().numCtx())
                .numPredict(props.chat().options().numPredict())
                .temperature(props.chat().options().temperature())
                .build();

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi1)
                .defaultOptions(options)
                .build();
    }

    @Bean
    public OllamaChatModel koreanChatModel(OllamaApi ollamaApi2, KoreanAIProperties props) {
        OllamaOptions options = OllamaOptions.builder()
                .model(props.chat().model())
                .format(props.chat().options().format())
                .numCtx(props.chat().options().numCtx())
                .numPredict(props.chat().options().numPredict())
                .temperature(props.chat().options().temperature())
                .build();

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi2)
                .defaultOptions(options)
                .build();
    }

    @Bean
    public OllamaChatModel japaneseChatModel(OllamaApi ollamaApi3, JapaneseAIProperties props) {
        OllamaOptions options = OllamaOptions.builder()
                .model(props.chat().model())
                .format(props.chat().options().format())
                .numCtx(props.chat().options().numCtx())
                .numPredict(props.chat().options().numPredict())
                .temperature(props.chat().options().temperature())
                .build();
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi3)
                .defaultOptions(options)
                .build();
    }

    // 3. Create ChatClient beans based on the specific ChatModels
    @Bean("qwen3")
    public ChatClient defaultChatClient(OllamaChatModel defaultChatModel) {
        return ChatClient.builder(defaultChatModel).defaultSystem(systemPrompt).build();
    }

    @Bean("exaone")
    public ChatClient koreanChatClient(OllamaChatModel koreanChatModel) {
        return ChatClient.builder(koreanChatModel).defaultSystem(systemPrompt).build();
    }

    @Bean("elyza")
    public ChatClient japaneseChatClient(OllamaChatModel japaneseChatModel) {
        return ChatClient.builder(japaneseChatModel).defaultSystem(japaneseSystemPrompt).build();
    }
}
