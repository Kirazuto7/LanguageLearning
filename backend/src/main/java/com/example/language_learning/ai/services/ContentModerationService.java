package com.example.language_learning.ai.services;


import com.example.language_learning.ai.dtos.moderation.ModerationRequest;
import com.example.language_learning.ai.dtos.moderation.ModerationResponse;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.modernmt.text.profanity.ProfanityFilter;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@Slf4j
public class ContentModerationService {
    private final WebClient webClient;
    private final RateLimiter moderationApiRateLimiter;
    private final Retry moderationApiRetry;
    private final ProfanityFilter profanityFilter;
    private final LanguageDetector languageDetector;

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    public ContentModerationService(WebClient.Builder webClientBuilder, RateLimiter moderationApiRateLimiter, Retry moderationApiRetry, LanguageDetector languageDetector) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.moderationApiRateLimiter = moderationApiRateLimiter;
        this.moderationApiRetry = moderationApiRetry;
        this.profanityFilter = new ProfanityFilter();
        this.languageDetector = languageDetector;
    }

    public Mono<Boolean> isContentFlagged(String text) {
        if (text == null || text.isBlank()) {
            return Mono.just(false);
        }

        ModerationRequest request = new ModerationRequest(text);

        return webClient.post()
                .uri("/moderations")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + openAiApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .map(response -> response.results() != null && !response.results().isEmpty() && response.results().getFirst().flagged())
                .transformDeferred(RateLimiterOperator.of(moderationApiRateLimiter))
                .transformDeferred(RetryOperator.of(moderationApiRetry))
                .doOnError(e -> log.error("Failed to call OpenAI Moderation API", e))
                .onErrorResume(e ->  {
                    Language detectedLanguage = this.languageDetector.detectLanguageOf(text);
                    String langCode;
                    if (detectedLanguage != Language.UNKNOWN) {
                        langCode = detectedLanguage.getIsoCode639_1().name().toLowerCase();
                    }
                    else {
                        langCode = "en";
                    }
                    return Mono.fromCallable(() -> this.profanityFilter.test(langCode, text));
                });
    }
}
