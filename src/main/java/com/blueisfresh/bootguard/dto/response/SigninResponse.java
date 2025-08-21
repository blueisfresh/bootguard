package com.blueisfresh.bootguard.dto.response;

import com.blueisfresh.bootguard.security.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after a successful user sign-in.
 * <p>
 * Contains:
 * <ul>
 *   <li>{@code accessToken} → short-lived JWT used for authenticated requests.</li>
 *   <li>{@code refreshToken} → opaque UUID string stored in the database, used to obtain new access tokens.</li>
 *   <li>{@code tokenType} → currently always {@code BEARER}.</li>
 * </ul>
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
