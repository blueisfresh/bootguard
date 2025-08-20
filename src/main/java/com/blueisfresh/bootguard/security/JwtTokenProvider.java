package com.blueisfresh.bootguard.security;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class responsible for creating and validating JWT tokens.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Generate short-lived access tokens.</li>
 *   <li>Generate long-lived refresh tokens.</li>
 *   <li>Extract claims (e.g., username) from tokens.</li>
 *   <li>Validate token integrity and expiration.</li>
 * </ul>
 */
@Component
public class JwtTokenProvider {

    private final Key jwtSecret;
    private final long jwtExpirationInMs;
    private final long refreshTokenExpirationInMs;

    public JwtTokenProvider(JwtProperties properties) {
        this.jwtSecret = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
        this.jwtExpirationInMs = properties.getExpirationMilliseconds();
        this.refreshTokenExpirationInMs = properties.getRefreshTokenExpirationMilliseconds();
    }

    /**
     * Generate a short-lived access token for the given username.
     */
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(jwtSecret, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a long-lived refresh token for the given username.
     */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationInMs))
                .signWith(jwtSecret, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the username (subject) from a JWT token.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validate the given JWT token.
     *
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
