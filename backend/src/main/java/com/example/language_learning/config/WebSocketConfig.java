package com.example.language_learning.config;

import com.example.language_learning.security.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.*;
import org.springframework.graphql.server.webmvc.GraphQlWebSocketHandler;
import org.springframework.http.HttpHeaders;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHandler;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Bean
    public WebSocketHandler webSocketHandler(WebGraphQlHandler graphQlHandler) {
        HttpMessageConverter<?> converter = new MappingJackson2HttpMessageConverter();
        return new GraphQlWebSocketHandler(graphQlHandler, converter, Duration.ofMinutes(15), Duration.ofMinutes(15));

    }

    @Bean
    public WebSocketGraphQlInterceptor webSocketGraphQlInterceptor() {
        return new WebSocketGraphQlInterceptor() {
            /** This method runs ONCE when the WebSocket connection is established.
             */
            @Override
            @NonNull
            public Mono<Object> handleConnectionInitialization(@NonNull WebSocketSessionInfo sessionInfo, @NonNull Map<String, Object> payload) {
                //log.info("SUBSCRIPTION COOKIE: {}", sessionInfo.getHeaders().getFirst(HttpHeaders.COOKIE));
                String authorizationHeader = (String) payload.get(HttpHeaders.AUTHORIZATION);
                Optional<String> token = jwtService.extractJwtFromCookieHeader(authorizationHeader);
                if (token.isEmpty()) {
                    log.info("No token in payload, falling back to cookie for session {}", sessionInfo.getId());
                    token = jwtService.extractJwtFromCookieHeader(sessionInfo.getHeaders().getFirst(HttpHeaders.COOKIE));
                }
                return token.map(this::authenticate)
                        .orElse(Mono.empty())
                        .doOnNext(authentication -> {
                            sessionInfo.getAttributes().put("user-authentication", authentication);
                            log.info("Stored authentication for user {} in Websocket session {}", authentication.getName(), sessionInfo.getId());
                        })
                        .thenReturn((Object) payload)
                        .switchIfEmpty(Mono.just((Object) payload));
            }
            /** This method runs for every GraphQL operation over the WebSocket (e.g., when a subscription is started).
            /*  Retrieve the Authentication object we stored during the handshake.
            */
            @Override
            @NonNull
            public Mono<WebGraphQlResponse> intercept(@NonNull WebGraphQlRequest request, @NonNull Chain chain) {
                Authentication authentication = (Authentication) request.getAttributes().get("user-authentication");
                if (authentication != null) {
                    return chain.next(request).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                }
                return chain.next(request);
            }

            private Mono<Authentication> authenticate(String jwt) {
                try {
                    String username = jwtService.extractUsername(jwt);
                    if (username != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                        }
                    }
                }
                catch (Exception e) {
                    log.warn("Invalid JWT in WebSocket handshake: {}", e.getMessage());
                }
                return Mono.empty();
            }
        };
    }


}
