package com.blueisfresh.bootguard.dto.response;

import com.blueisfresh.bootguard.security.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after a successful user sign-in.
 * <p>
 * Contains the generated access token, refresh token,
 * and the token type (e.g., BEARER).
 * <p>
 * Used by the client to authenticate subsequent requests
 * and to refresh the access token when it expires.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigninResponse {
    private String accessToken;
    private String refreshToken;
    private TokenType tokenType;
}
