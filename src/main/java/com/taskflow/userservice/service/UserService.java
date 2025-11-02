package com.taskflow.userservice.service;

import com.taskflow.userservice.dto.UpdateUserRequest;
import com.taskflow.userservice.model.User;
import com.taskflow.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Returns the currently authenticated user.
     * Uses the JWT subject (email) stored in the SecurityContext.
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(authentication.getName());
    }

    /**
     * Updates the authenticated user's basic profile: email and/or password.
     * - If email is provided, checks that it's not already used by another user.
     * - If password is provided, hashes it before saving.
     * - Returns Optional.empty() if no authenticated user is found.
     *
     * Note: Passwords are sent in plain text over TLS in real deployments.
     */
    public Optional<User> updateCurrentUser(UpdateUserRequest update) {
        return getCurrentUser().map(user -> {
            // Update email if provided and different
            String newEmail = update.getEmail();
            if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new RuntimeException("Email already in use");
                }
                user.setEmail(newEmail);
            }

            // Update password if provided
            String newPassword = update.getPassword();
            if (newPassword != null && !newPassword.isBlank()) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }

            return userRepository.save(user);
        });
    }

    /**
     * Updates any user (admin use or internal). Kept for completeness.
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
