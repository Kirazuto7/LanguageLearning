package com.example.language_learning.config;

import com.example.language_learning.lessonbook.chapter.lesson.dtos.LessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.LessonPageDTO;
import com.example.language_learning.shared.enums.LessonType;
import com.example.language_learning.shared.word.dtos.*;
import com.example.language_learning.storybook.shortstory.page.StoryPageDTO;
import com.example.language_learning.storybook.shortstory.page.StoryPageType;
import graphql.scalars.ExtendedScalars;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


@Configuration
public class GraphqlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        GraphQLScalarType localDateTimeScalar = GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .description("A custom scalar that handles Java's LocalDateTime")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult, GraphQLContext graphQLContext, Locale locale) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof LocalDateTime localDateTime) {
                        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    throw new CoercingSerializeException("Expected a LocalDateTime object.");
                }

                @Override
                public LocalDateTime parseValue(Object input, GraphQLContext graphQLContext, Locale locale) throws CoercingParseValueException {
                    try {
                        if (input instanceof String) {
                            return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                        throw new CoercingParseValueException("Expected a String");
                    }
                    catch (DateTimeParseException e) {
                        throw new CoercingParseValueException("Not a valid ISO_LOCAL_DATE_TIME: '" + input + "'.", e);
                    }
                }

                @Override
                public LocalDateTime parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) throws CoercingParseLiteralException {
                    if (input instanceof StringValue stringValue) {
                        try {
                            return LocalDateTime.parse(stringValue.getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                        catch (DateTimeParseException e) {
                            throw new CoercingParseLiteralException("Not a valid ISO_LOCAL_DATE_TIME: '" + stringValue.getValue() + "'.", e);
                        }
                    }
                    throw new CoercingParseLiteralException("Expected a StringValue.");
                }
            }).build();

        TypeResolver lessonTypeResolver = env -> {
            Object javaObject = env.getObject();
            if (javaObject instanceof LessonDTO lessonDTO) {
                return switch (lessonDTO.type()) {
                    case LessonType.VOCABULARY -> env.getSchema().getObjectType("VocabularyLesson");
                    case LessonType.GRAMMAR -> env.getSchema().getObjectType("GrammarLesson");
                    case LessonType.PRACTICE -> env.getSchema().getObjectType("PracticeLesson");
                    case LessonType.CONJUGATION -> env.getSchema().getObjectType("ConjugationLesson");
                    case LessonType.READING_COMPREHENSION -> env.getSchema().getObjectType("ReadingComprehensionLesson");
                    default -> null;
                };
            }
            return null;
        };

        TypeResolver progressDataTypeResolver = env -> {
            Object javaObject = env.getObject();
            if (javaObject instanceof LessonPageDTO) {
                return env.getSchema().getObjectType("LessonPage");
            }
            else if  (javaObject instanceof StoryPageDTO storyPage) {
               return switch (storyPage.type()) {
                   case StoryPageType.CONTENT -> env.getSchema().getObjectType("StoryContentPage");
                   case StoryPageType.VOCABULARY ->  env.getSchema().getObjectType("StoryVocabularyPage");
                   default -> null;
               };
            }
            return null;
        };

        TypeResolver storyPageTypeResolver = env -> {
            Object javaObject = env.getObject();
            if (javaObject instanceof StoryPageDTO storyPage) {
                return switch (storyPage.type()) {
                    case StoryPageType.CONTENT -> env.getSchema().getObjectType("StoryContentPage");
                    case StoryPageType.VOCABULARY ->  env.getSchema().getObjectType("StoryVocabularyPage");
                    default -> null;
                };
            }
            return null;
        };

        TypeResolver wordDetailsTypeResolver = env -> {
            Object javaObject = env.getObject();
            return switch (javaObject) {
                case JapaneseWordDetailsDTO j -> env.getSchema().getObjectType("JapaneseWordDetails");
                case KoreanWordDetailsDTO k -> env.getSchema().getObjectType("KoreanWordDetails");
                case ChineseWordDetailsDTO c -> env.getSchema().getObjectType("ChineseWordDetails");
                case ThaiWordDetailsDTO t -> env.getSchema().getObjectType("ThaiWordDetails");
                case ItalianWordDetailsDTO i -> env.getSchema().getObjectType("ItalianWordDetails");
                case SpanishWordDetailsDTO s -> env.getSchema().getObjectType("SpanishWordDetails");
                case FrenchWordDetailsDTO f -> env.getSchema().getObjectType("FrenchWordDetails");
                case GermanWordDetailsDTO g -> env.getSchema().getObjectType("GermanWordDetails");
                default -> null;
            };
        };

        return wiringBuilder -> wiringBuilder
            .scalar(ExtendedScalars.Json)
            .scalar(localDateTimeScalar)
            .type("Lesson", typeWiring -> typeWiring.typeResolver(lessonTypeResolver))
            .type("ProgressData", typeWiring -> typeWiring.typeResolver(progressDataTypeResolver))
            .type("StoryPage", typeWiring -> typeWiring.typeResolver(storyPageTypeResolver))
            .type("WordDetails", typeWiring -> typeWiring.typeResolver(wordDetailsTypeResolver));
    }
}
