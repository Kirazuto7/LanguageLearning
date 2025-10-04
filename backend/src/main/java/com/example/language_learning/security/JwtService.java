package com.example.language_learning.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateOnboardingToken(String email, String name) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", name);
        extraClaims.put("email", email);

        long onboardingTokenExpiration = 300000;

        return Jwts.builder()
                .claims(extraClaims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + onboardingTokenExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public void addJwtCookieToResponse(HttpServletResponse servletResponse, AuthenticationResponse response) {
        ResponseCookie cookie = createJwtCookie(response.token());
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearJwtCookieFromResponse(HttpServletResponse servletResponse) {
        ResponseCookie cookie = createLogoutCookie();
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void addRefreshTokenCookieToResponse(HttpServletResponse servletResponse, String refreshToken) {
        ResponseCookie cookie = createRefreshTokenCookie(refreshToken);
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearRefreshTokenCookieFromResponse(HttpServletResponse servletResponse) {
        ResponseCookie cookie = createLogoutRefreshTokenCookie();
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     *   Extracts the JWT from the "jwt-token" cookie in an HttpServletRequest.
     */
    public String extractJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "jwt-token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public String extractRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> "refresh-token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Extracts the JWT from a raw "Cookie" header string, used for WebSockets.
     */
    public Optional<String> extractJwtFromCookieHeader(String cookieHeader) {
        if (cookieHeader == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .filter(cookieStr -> cookieStr.startsWith("jwt-token="))
                .map(cookieStr -> cookieStr.substring("jwt-token=".length()))
                .findFirst();
    }

    public Optional<String> extractRefreshTokenFromCookieHeader(String cookieHeader) {
        if (cookieHeader == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .filter(cookieStr -> cookieStr.startsWith("refresh-token="))
                .map(cookieStr -> cookieStr.substring("refresh-token=".length()))
                .findFirst();
    }

    public ResponseCookie createJwtCookie(String jwt) {
        return ResponseCookie.from("jwt-token", jwt)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(jwtExpiration / 1000)
                .build();
    }

    private ResponseCookie createLogoutCookie() {
        return ResponseCookie.from("jwt-token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
                .build();
    }

    private ResponseCookie createLogoutRefreshTokenCookie() {
        return ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
