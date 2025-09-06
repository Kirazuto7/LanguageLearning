package com.example.language_learning.config;

import com.example.language_learning.dto.lessons.LessonDTO;
import com.example.language_learning.entity.lessons.*;
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
        return wiringBuilder -> wiringBuilder
                                .scalar(ExtendedScalars.Json)
                                .type("Lesson", typeWiring -> typeWiring.typeResolver(lessonTypeResolver));
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
