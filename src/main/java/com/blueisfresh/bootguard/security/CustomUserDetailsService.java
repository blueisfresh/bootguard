package com.blueisfresh.bootguard.security;

import com.blueisfresh.bootguard.entity.User;
import com.blueisfresh.bootguard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of UserDetailsService.
 * Loads user details from the database for Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username));

        // Convert your User entity into Spring Security's UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash()) // must be encoded with BCrypt
                .authorities(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .toArray(String[]::new))
                .build();
    }
}