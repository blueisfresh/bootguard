package com.blueisfresh.bootguard.config;

import com.blueisfresh.bootguard.entity.Role;
import com.blueisfresh.bootguard.repository.RoleRepository;
import com.blueisfresh.bootguard.user.RoleName;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * Configuration class responsible for seeding initial data into the database.
 * <p>
 * Specifically, this ensures that the default roles {@code ROLE_USER} and {@code ROLE_ADMIN}
 * exist in the database when the application starts. If they are missing, they will be created.
 * <p>
 * This allows the application to always have the required roles available for user registration
 * and authorization without requiring manual inserts.
 */

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
                roleRepository.save(new Role(null, RoleName.ROLE_USER, new ArrayList<>()));
            }
            if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {
                roleRepository.save(new Role(null, RoleName.ROLE_ADMIN, new ArrayList<>()));
            }
        };
    }
}
