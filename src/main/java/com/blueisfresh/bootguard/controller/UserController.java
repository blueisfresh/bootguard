package com.blueisfresh.bootguard.controller;

import com.blueisfresh.bootguard.dto.UserDto;
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
    public ResponseEntity<UserDto> getCurrentUser() {
        String username = authUtils.getCurrentUsername();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto request) {
        return ResponseEntity.ok(userService.updateCurrentUser(request));
    }
}
