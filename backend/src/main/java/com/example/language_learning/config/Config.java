package com.example.language_learning.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


@Configuration
public class Config {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, @Value("${app.ai.system-prompt}") String systemPrompt) {
        return builder.defaultSystem(systemPrompt).build();
    }

    /*@Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }*/

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modulesToInstall(JavaTimeModule.class);
        return builder;
    }
    @Bean ObjectMapper objectMapper() { ObjectMapper objectMapper = new ObjectMapper(); objectMapper.registerModule(new JavaTimeModule()); return objectMapper; }
}