package com.blueisfresh.bootguard.repository;

import com.blueisfresh.bootguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // for login
    Optional<User> findByUsername(String username);

    // for login or profile lookup
    Optional<User> findByEmail(String email);

    // for signup validation
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
