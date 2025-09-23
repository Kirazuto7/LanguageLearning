package com.example.language_learning.shared.utils;

import com.example.language_learning.shared.word.data.WordDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Converter
@Slf4j
public class JpaMapConverter implements AttributeConverter<WordDetails, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JpaMapConverter() {
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String convertToDatabaseColumn(WordDetails attribute) {
        if(attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch(JsonProcessingException e) {
            log.error("Error converting WordDetails to JSON string", e);
            throw new IllegalArgumentException("Error converting WordDetails to JSON string", e);
        }
    }

    @Override
    public WordDetails convertToEntityAttribute(String dbData) {
        if(dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, WordDetails.class);
        } catch(IOException e) {
            log.error("Error converting JSON string to WordDetails", e);
            throw new IllegalArgumentException("Error converting JSON string to WordDetails", e);
        }
    }
}
