package com.example.language_learning.ai.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Language {
    //exaone - kr/jp
    //qwen3
    JAPANESE("japanese", "qwen3", SchemaGroup.JAPANESE, VocabularySchema.JAPANESE, InstructionGroup.JAPANESE),
    KOREAN("korean", "qwen3", SchemaGroup.KOREAN, VocabularySchema.KOREAN, InstructionGroup.KOREAN),
    CHINESE("chinese", "qwen3", SchemaGroup.CHINESE, VocabularySchema.CHINESE, InstructionGroup.CHINESE),
    THAI("thai", "qwen3", SchemaGroup.THAI, VocabularySchema.THAI, InstructionGroup.THAI),
    ITALIAN("italian", "qwen3", SchemaGroup.LATIN_EXTENDED, VocabularySchema.ITALIAN, InstructionGroup.ITALIAN),
    SPANISH("spanish", "qwen3", SchemaGroup.LATIN_EXTENDED, VocabularySchema.SPANISH, InstructionGroup.SPANISH),
    FRENCH("french", "qwen3", SchemaGroup.LATIN_EXTENDED, VocabularySchema.FRENCH, InstructionGroup.FRENCH),
    GERMAN("german", "qwen3", SchemaGroup.LATIN_EXTENDED, VocabularySchema.GERMAN, InstructionGroup.GERMAN);

    private final String value;
    private final String modelName;
    private final SchemaGroup schemaGroup;
    private final VocabularySchema vocabularySchema;
    private final InstructionGroup instructionGroup;

    private static final Map<String, Language> stringToEnum =
            Arrays.stream(values()).collect(Collectors.toMap(Language::getValue, Function.identity()));

    public static Language fromString(String value) {
        return stringToEnum.get(value.toLowerCase());
    }
}