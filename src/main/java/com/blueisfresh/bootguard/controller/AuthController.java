package com.blueisfresh.bootguard.controller;

import com.blueisfresh.bootguard.dto.UserDto;
import com.blueisfresh.bootguard.dto.request.SigninRequest;
import com.blueisfresh.bootguard.dto.request.SignupRequest;
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
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserDto user = userService.registerUser(request);

        // builds Response Dto
        SignupResponse response = SignupResponse.builder().message("User registered successfully").userId(user.getId()).username(user.getUsername()).build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest request) {

        // Authenticate the user with username/password and store the result in the Spring Security
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateAccessToken(request.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());

        // builds Response Dto
        SigninResponse response = SigninResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType(TokenType.BEARER).build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<SigninResponse> refresh(@RequestBody Map<String, String> request) {

        // get the refreshtoken from the request
        String refreshToken = request.get("refreshToken");

        // validate refresh token; if valid issue a new access token; otherwise return 400 bad request
        return refreshTokenService.findByToken(refreshToken)
                .filter(token -> !refreshTokenService.isTokenExpired(token))
                .map(token -> {
                    String newAccessToken = jwtTokenProvider.generateAccessToken(token.getUser().getUsername());
                    return ResponseEntity.ok(
                            new SigninResponse(newAccessToken, refreshToken, TokenType.BEARER)
                    );
                })
                .orElse(ResponseEntity.badRequest().build());
    }
}
