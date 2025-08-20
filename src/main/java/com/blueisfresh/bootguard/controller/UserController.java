package com.blueisfresh.bootguard.controller;

import com.blueisfresh.bootguard.dto.UserDto;
import com.blueisfresh.bootguard.dto.response.ApiResponse;
import com.blueisfresh.bootguard.service.UserService;
import com.blueisfresh.bootguard.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthUtils authUtils;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {

        // Current Userinformation Search
        String username = authUtils.getCurrentUsername();
        UserDto userInformation = userService.getUserByUsername(username);

        // Wrap into Api Response
        return ResponseEntity.ok(ApiResponse.<UserDto>builder()
                .success(true)
                .data(userInformation)
                .message("Getting your Current User Information was a success")
                .build());
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByUsername(@PathVariable String username) {

        // Search for User
        UserDto foundUser = userService.getUserByUsername(username);

        // Wrap into Api Response
        return ResponseEntity.ok(ApiResponse.<UserDto>builder()
                .success(true)
                .data(foundUser)
                .message("User Found!")
                .build());
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@Valid @RequestBody UserDto request) {

        // Update Current User
        UserDto updatedUser = userService.updateCurrentUser(request);

        return ResponseEntity.ok(ApiResponse.<UserDto>builder()
                .success(true)
                .data(updatedUser)
                .message("User successfully updated!")
                .build());
    }
}
