package com.example.language_learning.config;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.scalars.ExtendedScalars;
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
        return wiringBuilder -> wiringBuilder.scalar(ExtendedScalars.Json);
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
