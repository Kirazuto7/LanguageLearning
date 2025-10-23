package com.example.language_learning.config;

import com.example.language_learning.security.JwtService;
import com.example.language_learning.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;


@Configuration
@Slf4j
public class WebSocketGraphQlConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public WebSocketGraphQlConfig(JwtService jwtService, UserDetailsService userDetailsService, @Lazy UserService userService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Bean
    public GraphQlWebSocketHandler graphQlWebSocketHandler(WebGraphQlHandler graphQlHandler) {
        HttpMessageConverter<?> converter = new MappingJackson2HttpMessageConverter();
        Duration initTimeout = Duration.ofMinutes(1);
        Duration keepAliveDuration = Duration.ofSeconds(15);
        return new GraphQlWebSocketHandler(graphQlHandler, converter, initTimeout, keepAliveDuration);

    }

    @Bean
    public WebSocketGraphQlInterceptor webSocketGraphQlInterceptor() {
        return new WebSocketGraphQlInterceptor() {
            /** This method runs ONCE when the WebSocket connection is established.
             */
            @Override
            @NonNull
            public Mono<Object> handleConnectionInitialization(@NonNull WebSocketSessionInfo sessionInfo, @NonNull Map<String, Object> payload) {
                String authorizationHeader = (String) payload.get(HttpHeaders.AUTHORIZATION);
                Optional<String> token = jwtService.extractJwtFromCookieHeader(authorizationHeader);
                if (token.isEmpty()) {
                    log.info("No token in payload, falling back to cookie for session {}", sessionInfo.getId());
                    token = jwtService.extractJwtFromCookieHeader(sessionInfo.getHeaders().getFirst(HttpHeaders.COOKIE));
                }
                return token.map(this::authenticate)
                        .orElse(Mono.empty())
                        .switchIfEmpty(Mono.defer(() -> {
                            log.debug("Access token failed or not present, attempting to authenticate with refresh token for session {}", sessionInfo.getId());
                            String cookieHeader = sessionInfo.getHeaders().getFirst(HttpHeaders.COOKIE);
                            return jwtService.extractRefreshTokenFromCookieHeader(cookieHeader)
                                    .map(refreshToken -> {
                                        try {
                                            UserDetails userDetails = userService.loadUserByRefreshToken(refreshToken);
                                            log.info("Successfully authenticated user {} via refresh token during WebSocket handshake.", userDetails.getUsername());
                                            return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                                        }
                                        catch (Exception e) {
                                            log.warn("Refresh token validation failed during WebSocket handshake: {}", e.getMessage());
                                            return Mono.<Authentication>empty();
                                        }
                                    })
                                    .orElse(Mono.empty());
                        }))
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
