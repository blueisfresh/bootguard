package com.blueisfresh.bootguard.controller;

import com.blueisfresh.bootguard.dto.UserDto;
import com.blueisfresh.bootguard.dto.request.SigninRequest;
import com.blueisfresh.bootguard.dto.request.SignupRequest;
import com.blueisfresh.bootguard.dto.response.ApiResponse;
import com.blueisfresh.bootguard.dto.response.SigninResponse;
import com.blueisfresh.bootguard.dto.response.SignupResponse;
import com.blueisfresh.bootguard.security.JwtTokenProvider;
import com.blueisfresh.bootguard.security.RefreshTokenService;
import com.blueisfresh.bootguard.security.TokenType;
import com.blueisfresh.bootguard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {

        UserDto user = userService.registerUser(request);

        // Build Signup Response
        SignupResponse response = SignupResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .build();

        // Wrap in ApiResponse
        return ResponseEntity.ok(
                ApiResponse.<SignupResponse>builder()
                        .success(true)
                        .message("User registered successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SigninResponse>> signin(@Valid @RequestBody SigninRequest request) {

        // Authenticate the user with username/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(request.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());

        // Build Signin Response
        SigninResponse response = SigninResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .build();

        // Wrap in ApiResponse
        return ResponseEntity.ok(
                ApiResponse.<SigninResponse>builder()
                        .success(true)
                        .message("User signed in successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<SigninResponse>> refresh(@RequestBody Map<String, String> request) {

        // Get Refresh-token from the request
        String refreshToken = request.get("refreshToken");

        // Validate refresh token; If valid issue a new access token; Otherwise return 400 bad request
        return refreshTokenService.findByToken(refreshToken)
                .filter(token -> !refreshTokenService.isTokenExpired(token))
                .map(token -> {
                    String newAccessToken = jwtTokenProvider.generateAccessToken(token.getUser().getUsername());
                    SigninResponse response = SigninResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(refreshToken)
                            .tokenType(TokenType.BEARER)
                            .build();

                    return ResponseEntity.ok(
                            ApiResponse.<SigninResponse>builder()
                                    .success(true)
                                    .message("Successfully refreshed access token")
                                    .data(response)
                                    .build()
                    );
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(
                        ApiResponse.<SigninResponse>builder()
                                .success(false)
                                .message("Invalid or expired refresh token")
                                .data(null) // explicitly null
                                .build()
                ));
    }
}
