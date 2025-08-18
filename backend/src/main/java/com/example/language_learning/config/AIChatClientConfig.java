package com.example.language_learning.config;

import com.example.language_learning.config.properties.ExaoneProperties;
import com.example.language_learning.config.properties.Qwen3Properties;
import com.example.language_learning.config.properties.RakutenProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Manually configures the ChatClient beans for each AI model.
 * This provides full control and avoids auto-configuration issues.
 */
@Configuration
@EnableConfigurationProperties({Qwen3Properties.class, ExaoneProperties.class, RakutenProperties.class})
public class AIChatClientConfig {

    @Value("${app.ai.system-prompt}")
    private String systemPrompt;

    // 1. Create separate OllamaApi beans for each instance using the builder
    @Bean
    public OllamaApi ollamaApi1(Qwen3Properties props) {
        return OllamaApi.builder().baseUrl(props.baseUrl()).build();
    }

    @Bean
    public OllamaApi ollamaApi2(ExaoneProperties props) {
        return OllamaApi.builder().baseUrl(props.baseUrl()).build();
    }

    @Bean
    public OllamaApi ollamaApi3(RakutenProperties props) {
        return OllamaApi.builder().baseUrl(props.baseUrl()).build();
    }

    // 2. Create OllamaChatModel beans using the builder pattern
    @Bean
    public OllamaChatModel qwen3ChatModel(OllamaApi ollamaApi1, Qwen3Properties props) {
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
    public OllamaChatModel exaoneChatModel(OllamaApi ollamaApi2, ExaoneProperties props) {
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
    public OllamaChatModel rakutenChatModel(OllamaApi ollamaApi3, RakutenProperties props) {
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
    public ChatClient qwen3ChatClient(OllamaChatModel qwen3ChatModel) {
        return ChatClient.builder(qwen3ChatModel).defaultSystem(systemPrompt).build();
    }

    @Bean("exaone")
    public ChatClient exaoneChatClient(OllamaChatModel exaoneChatModel) {
        return ChatClient.builder(exaoneChatModel).defaultSystem(systemPrompt).build();
    }

    @Bean("rakuten")
    public ChatClient rakutenChatClient(OllamaChatModel rakutenChatModel) {
        return ChatClient.builder(rakutenChatModel).defaultSystem(systemPrompt).build();
    }

}
