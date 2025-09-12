package com.example.language_learning.config;

import com.example.language_learning.dto.lessons.LessonDTO;
import com.example.language_learning.dto.models.details.*;
import com.example.language_learning.dto.models.PageDTO;
import com.example.language_learning.enums.LessonType;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.scalars.ExtendedScalars;
import graphql.schema.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Configuration
public class GraphqlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
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
            if (javaObject instanceof PageDTO) {
                return env.getSchema().getObjectType("Page");
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
            .type("Lesson", typeWiring -> typeWiring.typeResolver(lessonTypeResolver))
            .type("ProgressData", typeWiring -> typeWiring.typeResolver(progressDataTypeResolver))
            .type("WordDetails", typeWiring -> typeWiring.typeResolver(wordDetailsTypeResolver));
    }

    @Bean
    public DataFetcherExceptionResolver exceptionResolver() {
        return (exception, environment) -> {
            if (exception instanceof SecurityException) {
                GraphQLError error = GraphQLError.newError()
                        .errorType(ErrorType.DataFetchingException)
                        .message(exception.getMessage())
                        .extensions(Map.of("classification", "UNAUTHORIZED"))
                        .path(environment.getExecutionStepInfo().getPath())
                        .build();
                return Mono.just(List.of(error));
            }
            return Mono.empty();
        };
    }
}
