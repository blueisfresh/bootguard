package com.blueisfresh.bootguard.service;

import com.blueisfresh.bootguard.dto.UserDto;
import com.blueisfresh.bootguard.dto.request.SignupRequest;
import com.blueisfresh.bootguard.entity.Role;
import com.blueisfresh.bootguard.entity.User;
import com.blueisfresh.bootguard.exception.EmailAlreadyExistsException;
import com.blueisfresh.bootguard.exception.UsernameAlreadyExistsException;
import com.blueisfresh.bootguard.mapper.UserMapper;
import com.blueisfresh.bootguard.repository.RoleRepository;
import com.blueisfresh.bootguard.repository.UserRepository;
import com.blueisfresh.bootguard.user.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for managing user-related operations.
 * <p>
 * Handles user registration, profile updates, and user lookups.
 * Ensures uniqueness of usernames and emails.
 */

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    // Helper Method for validation
    private void validateUniqueUser(String username, String email) {
        userRepository.findByUsernameOrEmail(username, email).ifPresent(user -> {
            if (user.getUsername().equals(username)) {
                throw new UsernameAlreadyExistsException("Username '" + username + "' is already taken.");
            }
            if (user.getEmail().equals(email)) {
                throw new EmailAlreadyExistsException("Email '" + email + "' is already registered.");
            }
        });
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    public UserDto updateCurrentUser(UserDto userRequest) {

        // find existing User
        User existingUser = userRepository.findById(userRequest.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userRequest.getId()));

        // Check if Username already exists
        if (userRequest.getUsername() != null && !userRequest.getUsername().equals(existingUser.getUsername()) && userRepository.existsByUsername(userRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username '" + userRequest.getUsername() + "' is already taken.");
        }

        // fields wont be overwritten if null
        if (userRequest.getEmail() != null) {
            existingUser.setEmail(userRequest.getEmail());
        }
        if (userRequest.getUsername() != null) {
            existingUser.setUsername(userRequest.getUsername());
        }
        if(userRequest.getDisplayName() != null) {
            existingUser.setDisplayName(userRequest.getDisplayName());
        }

        // save with repository
        userRepository.save(existingUser);

        // return new user
        return userMapper.toDto(existingUser);
    }

    public UserDto registerUser(SignupRequest request) {

        // validation
        validateUniqueUser(request.getUsername(), request.getEmail());

        // Map dto to user entity
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setDisplayName(request.getDisplayName());

        // Assign default role
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        newUser.getRoles().add(userRole);

        // save with repository
        userRepository.save(newUser);

        // return new user
        return userMapper.toDto(newUser);
    }
}
