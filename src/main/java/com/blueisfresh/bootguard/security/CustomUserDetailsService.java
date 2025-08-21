package com.blueisfresh.bootguard.security;

import com.blueisfresh.bootguard.entity.User;
import com.blueisfresh.bootguard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom implementation of Spring Security's {@link org.springframework.security.core.userdetails.UserDetailsService}.
 * <p>
 * Loads user details from the database and adapts them into Spring Security's
 * {@link org.springframework.security.core.userdetails.UserDetails}.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Look up a user by username.</li>
 *   <li>Map user roles to Spring Security {@link org.springframework.security.core.GrantedAuthority} objects.</li>
 *   <li>Provide user credentials and authorities to Spring Security for authentication.</li>
 * </ul>
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

        // Map roles to authorities
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}