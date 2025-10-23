package com.example.language_learning.shared.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * A static helper class designed to provide a consistent, pretty-printed JSON
 * representation for JPA entities in their toString() method. This avoids the
 * common pitfalls of overriding toString() directly in entities, such as
 * LazyInitializationException and circular reference errors.
 */
@Slf4j
public class JsonToStringHelper {
    /**
     * A single, static ObjectMapper instance that is configured for pretty-printing
     * and handling Hibernate-specific types. It is initialized once at application startup.
     */
    private static ObjectMapper mapper;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JsonToStringHelper() {}

    /**
     * Initializes the helper with a pre-configured ObjectMapper instance.
     * This method is called once at application startup from AppConfig.
     * @param objectMapper The configured ObjectMapper to use for serialization.
     */
    public static void setMapper(ObjectMapper objectMapper) {
        mapper = objectMapper;
    }

    /**
     * Serializes the given object into a pretty-printed JSON string.
     * This method is called by the overridden toString() method in BaseEntity.
     * @param object The object to serialize.
     * @return A JSON string representation of the object, or an error message if serialization fails.
     */
    public static String toJson(Object object) {
        if (mapper == null) {
            return "JsonToStringHelper not initialized. Default toString(): " + object.toString();
        }
        try {
            return mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON for toString().", e);
            return "Error during JSON serialization: " + e.getMessage();
        }
    }
}
