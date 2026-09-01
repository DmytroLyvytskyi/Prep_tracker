package com.example.Prep_tracker.controller;

import com.example.Prep_tracker.dto.LoginRequest;
import com.example.Prep_tracker.dto.RegisterRequest;
import com.example.Prep_tracker.dto.UserResponse;
import com.example.Prep_tracker.entity.User;
import com.example.Prep_tracker.security.JwtService;
import com.example.Prep_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail()));
    }

    public record AuthResponse(String token, Long userId, String email) {}
}