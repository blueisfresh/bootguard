package com.blueisfresh.bootguard.controller;

import com.blueisfresh.bootguard.dto.UserDto;
import com.blueisfresh.bootguard.dto.request.SigninRequest;
import com.blueisfresh.bootguard.dto.request.SignupRequest;
import com.blueisfresh.bootguard.dto.response.ApiResponse;
import com.blueisfresh.bootguard.dto.response.SigninResponse;
import com.blueisfresh.bootguard.dto.response.SignupResponse;
import com.blueisfresh.bootguard.entity.RefreshToken;
import com.blueisfresh.bootguard.entity.User;
import com.blueisfresh.bootguard.repository.UserRepository;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Authentication controller handling signup, signin, and token refresh.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Signup: Registers a new user with default role {@code ROLE_USER}.</li>
 *   <li>Signin: Authenticates user credentials, issues a short-lived JWT access token
 *       and a long-lived refresh token stored in the database.</li>
 *   <li>Refresh: Validates a refresh token from the database and issues a new access token.</li>
 * </ul>
 * <p>
 * Access token = JWT (stateless, short-lived).<br>
 * Refresh token = Random UUID stored in {@code refresh_tokens} table.
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

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

        // Generate access token
        String accessToken = jwtTokenProvider.generateAccessToken(request.getUsername());

        // Fetch user entity
        UserDto userDto = userService.getUserByUsername(request.getUsername());
        User userEntity = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and persist refresh token in DB
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                userEntity,
                jwtTokenProvider.getRefreshTokenExpirationInMs()
        );

        // Build Signin Response
        SigninResponse response = SigninResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
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

    // TODO: refreshtoken request dto
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
                            .refreshToken(refreshToken) // reuse same refresh token
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
                                .data(null)
                                .build()
                ));
    }
}
