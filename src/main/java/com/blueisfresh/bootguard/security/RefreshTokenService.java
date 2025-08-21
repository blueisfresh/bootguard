package com.blueisfresh.bootguard.security;

import com.blueisfresh.bootguard.entity.RefreshToken;
import com.blueisfresh.bootguard.entity.User;
import com.blueisfresh.bootguard.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing refresh tokens.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Create and persist a new refresh token (random UUID) for a user.</li>
 *   <li>Find a refresh token by its string value.</li>
 *   <li>Check if a refresh token is expired.</li>
 *   <li>Delete all refresh tokens for a user (e.g., on logout).</li>
 * </ul>
 * <p>
 * Refresh tokens are opaque UUIDs stored in the database, not JWTs.
 */

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Create and persist a new refresh token for the given user.
     */
    public RefreshToken createRefreshToken(User user, long durationMs) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(durationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Find a refresh token by its token string.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Check if the given refresh token is expired.
     */
    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    /**
     * Delete all refresh tokens belonging to the given user.
     */
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByToken(user);
    }
}
