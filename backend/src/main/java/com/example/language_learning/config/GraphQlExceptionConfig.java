package com.example.language_learning.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.util.Map;

@Slf4j
@Configuration
public class GraphQlExceptionConfig {

    @Bean
    public DataFetcherExceptionResolverAdapter exceptionResolver() {
        return new DataFetcherExceptionResolverAdapter() {
            @Override
            protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
                // Handle specific security exceptions first
                if (ex instanceof AuthorizationDeniedException) {
                    return GraphqlErrorBuilder.newError(env)
                            .message(ex.getMessage())
                            .errorType(ErrorType.UNAUTHORIZED)
                            .extensions(Map.of("classification", "UNAUTHORIZED"))
                            .build();
                }

                // For all other exceptions, log them in detail for debugging
                log.error("Unhandled exception during GraphQL data fetching: Path [{}], Exception [{}], Message [{}]",
                        env.getExecutionStepInfo().getPath(), ex.getClass().getName(), ex.getMessage(), ex);

                // And return a generic error to the client
                return GraphqlErrorBuilder.newError(env)
                        .message("An internal server error occurred. Please check server logs for details.")
                        .errorType(ErrorType.INTERNAL_ERROR)
                        .build();
            }
        };
    }
}