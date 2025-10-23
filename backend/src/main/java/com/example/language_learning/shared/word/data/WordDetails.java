package com.example.language_learning.shared.word.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = JapaneseWordDetails.class, name = "JapaneseWord"),
    @JsonSubTypes.Type(value = KoreanWordDetails.class, name = "KoreanWord"),
    @JsonSubTypes.Type(value = ChineseWordDetails.class, name = "ChineseWord"),
    @JsonSubTypes.Type(value = ThaiWordDetails.class, name = "ThaiWord"),
    @JsonSubTypes.Type(value = ItalianWordDetails.class, name = "ItalianWord"),
    @JsonSubTypes.Type(value = SpanishWordDetails.class, name = "SpanishWord"),
    @JsonSubTypes.Type(value = FrenchWordDetails.class, name = "FrenchWord"),
    @JsonSubTypes.Type(value = GermanWordDetails.class, name = "GermanWord")
})
public interface WordDetails {}
