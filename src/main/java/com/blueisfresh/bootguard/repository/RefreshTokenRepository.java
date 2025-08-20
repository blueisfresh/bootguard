package com.blueisfresh.bootguard.repository;

import com.blueisfresh.bootguard.entity.RefreshToken;
import com.blueisfresh.bootguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // to validate refresh tokens
    Optional<RefreshToken> findByToken(String token);

    // to revoke all refresh tokens for a user (ex.: on logout)
    int deleteByToken(User user);
}
