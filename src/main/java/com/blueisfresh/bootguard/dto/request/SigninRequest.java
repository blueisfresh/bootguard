package com.blueisfresh.bootguard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user sign-in requests.
 * <p>
 * Carries the username and password provided by the client
 * when attempting to authenticate.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigninRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}