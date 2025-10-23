package com.example.language_learning.ai.config;

import com.example.language_learning.ai.config.properties.Client1Properties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Manually configures the ChatClient beans for each AI model.
 * This provides full control and avoids auto-configuration issues.
 */
@Configuration
@EnableConfigurationProperties({Client1Properties.class/*, Client2Properties.class*/})
public class AIChatClientConfig {

    @Value("classpath:prompts/system/system_prompt.txt")
    private Resource systemPrompt;

    // 1. Create separate OllamaApi beans for each instance using the builder
    @Bean
    public OllamaApi ollamaApi1(Client1Properties props) {
        return OllamaApi.builder()
                .baseUrl(props.baseUrl()).build();
    }

    /*@Bean
    public OllamaApi ollamaApi2(Client2Properties props) {
        return OllamaApi.builder()
                .baseUrl(props.baseUrl()).build();
    }*/

    // 2. Create OllamaChatModel beans using the builder pattern
    @Bean
    public OllamaChatModel client1ChatModel(OllamaApi ollamaApi1, Client1Properties props) {
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

    /*@Bean
    public OllamaChatModel client2ChatModel(OllamaApi ollamaApi2, Client2Properties props) {
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
    }*/


    // 3. Create ChatClient beans based on the specific ChatModels
    @Bean("qwen3")
    public ChatClient chatClient1(OllamaChatModel client1ChatModel) {
        return ChatClient.builder(client1ChatModel).defaultSystem(systemPrompt).build();
    }

    /*@Bean("deepseek")
    public ChatClient chatClient2(OllamaChatModel client2ChatModel) {
        return ChatClient.builder(client2ChatModel).defaultSystem(systemPrompt).build();
    }*/
}
