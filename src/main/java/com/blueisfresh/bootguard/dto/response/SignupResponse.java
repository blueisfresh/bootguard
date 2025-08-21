package com.blueisfresh.bootguard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO returned after a successful user registration.
 * <p>
 * Contains the new user's ID and username.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {
    private UUID userId;
    private String username;
}
