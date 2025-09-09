package com.example.language_learning.entity.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GenericWordDetails.class, name = "GenericWord"),
    @JsonSubTypes.Type(value = JapaneseWordDetails.class, name = "JapaneseWord")
})
public interface WordDetails {}
