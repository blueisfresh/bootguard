package com.blueisfresh.bootguard.config;

import com.blueisfresh.bootguard.entity.Role;
import com.blueisfresh.bootguard.repository.RoleRepository;
import com.blueisfresh.bootguard.user.RoleName;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

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
