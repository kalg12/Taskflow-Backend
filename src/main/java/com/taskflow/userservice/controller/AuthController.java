package com.taskflow.userservice.controller;

import com.taskflow.userservice.dto.*;
import com.taskflow.userservice.model.User;
import com.taskflow.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // English: register endpoint (creates user and returns created entity)
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // English: login endpoint (validates credentials and returns JWT token)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
