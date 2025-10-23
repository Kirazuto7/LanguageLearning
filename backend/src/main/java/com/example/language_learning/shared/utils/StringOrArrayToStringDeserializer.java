package com.example.language_learning.shared.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class StringOrArrayToStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
            jsonParser.nextToken(); // Move to the first element

            if (jsonParser.currentToken() == JsonToken.END_ARRAY) {
                return null;
            }
            String value = jsonParser.getText();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) { /* consume the remaining tokens */}
            return value;
        }
        else if (jsonParser.currentToken() == JsonToken.VALUE_STRING) {
            return jsonParser.getText();
        }
        return null;
    }
}
