package com.blueisfresh.bootguard.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.xml.bind.DatatypeConverter;

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
        this.jwtSecret = Keys.hmacShaKeyFor(DatatypeConverter.parseHexBinary(properties.getSecret()));
        this.jwtExpirationInMs = properties.getExpirationMilliseconds();
        this.refreshTokenExpirationInMs = properties.getRefreshTokenExpirationMilliseconds();

        System.out.println("DEBUG: jwtExpirationInMs = " + this.jwtExpirationInMs);
        System.out.println("DEBUG: refreshTokenExpirationInMs = " + this.refreshTokenExpirationInMs);
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
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
