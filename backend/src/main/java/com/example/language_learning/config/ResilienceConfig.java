package com.example.language_learning.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public RateLimiter moderationApiRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(3)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(10))
                .build();
        return RateLimiter.of("moderationApi", config);
    }

    @Bean
    public Retry moderationApiRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofSeconds(2))
                .retryOnException(throwable -> throwable instanceof WebClientResponseException ex &&
                    ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS ||
                    throwable instanceof RequestNotPermitted)
                .build();
        return Retry.of("moderationApiRetry", config);
    }
}
