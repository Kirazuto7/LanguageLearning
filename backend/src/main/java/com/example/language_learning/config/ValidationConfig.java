package com.example.language_learning.config;

import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {
    @Bean
    public JsonSchemaFactory jsonSchemaFactory() {
        return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    }
}
