package com.blueisfresh.bootguard.repository;

import com.blueisfresh.bootguard.entity.RefreshToken;
import com.blueisfresh.bootguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link com.blueisfresh.bootguard.entity.RefreshToken} entities.
 * <p>
 * Provides methods for:
 * <ul>
 *   <li>Finding refresh tokens by their token string.</li>
 *   <li>Deleting all refresh tokens belonging to a specific user (e.g., on logout).</li>
 * </ul>
 */

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // to validate refresh tokens
    Optional<RefreshToken> findByToken(String token);

    // to revoke all refresh tokens for a user (ex.: on logout)
    int deleteByToken(User user);
}
