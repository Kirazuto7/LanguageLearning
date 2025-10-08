package com.example.language_learning.security;

import com.example.language_learning.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String refreshToken = jwtService.extractRefreshTokenFromRequest(request);
        userService.logout(refreshToken);
        jwtService.clearJwtCookieFromResponse(response);
        jwtService.clearRefreshTokenCookieFromResponse(response);
        log.info("Logout processed. All tokens invalidated and cookies cleared.");

    }
}
