package com.blueisfresh.bootguard.dto.response;

import com.blueisfresh.bootguard.security.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigninResponse {
    private String accessToken;
    private String refreshToken;
    private TokenType tokenType;
}
