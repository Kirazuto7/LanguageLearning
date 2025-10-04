package com.example.language_learning.security;

import com.example.language_learning.user.User;
import com.example.language_learning.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OidcAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {
            // --- SCENARIO 1: USER EXISTS ---
            log.info("OIDC user {} already exists. Logging them in.", email);
            // Generate tokens and set as cookie headers
            AuthenticationResponse authResponse = userService.buildAuthenticationResponseForOidcUser(email);
            jwtService.addJwtCookieToResponse(response, authResponse);
            jwtService.addRefreshTokenCookieToResponse(response, authResponse.refreshToken());

            response.sendRedirect("http://localhost:3000/home");
        }
        else {
            // --- SCENARIO 2: NEW USER ---
            log.info("OIDC user {} is new. Starting onboarding process.", email);
            String onboardingToken = jwtService.generateOnboardingToken(email, name);
            String encodedToken = UriUtils.encode(onboardingToken, StandardCharsets.UTF_8);
            String redirectUrl = "http://localhost:3000/welcome/oidc?token=" + encodedToken;
            response.sendRedirect(redirectUrl);
        }
    }
}
