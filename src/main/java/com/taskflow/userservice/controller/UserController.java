package com.taskflow.userservice.controller;

import com.taskflow.userservice.dto.UpdateUserRequest;
import com.taskflow.userservice.model.User;
import com.taskflow.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // this endpoint is protected by the JwtAuthFilter and SecurityConfig.
    // It returns the current user based on the JWT subject (email).
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return userService.getCurrentUser()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * Allows the authenticated user to update their basic profile info:
     * - email: will be checked for uniqueness
     * - password: will be hashed before saving
     *
     * Example: PUT /users/me with body { "email": "new@e.com", "password": "newpass" }
     */
    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@RequestBody UpdateUserRequest update) {
        try {
            return userService.updateCurrentUser(update)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(401).build());
        } catch (RuntimeException ex) {
            // return bad request if email already in use or other validation failure
            return ResponseEntity.badRequest().build();
        }
    }
}
