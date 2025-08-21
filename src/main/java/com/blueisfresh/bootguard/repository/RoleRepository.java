package com.blueisfresh.bootguard.repository;

import com.blueisfresh.bootguard.entity.Role;
import com.blueisfresh.bootguard.user.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link com.blueisfresh.bootguard.entity.Role} entities.
 * <p>
 * Provides methods for finding roles by name and checking if roles exist.
 */

public interface RoleRepository extends JpaRepository<Role, UUID> {

    // asign Roles during signup
    Optional<Role> findByName(RoleName name);

    // check if roles are seeded in DB
    boolean existsByName(RoleName name);
}
