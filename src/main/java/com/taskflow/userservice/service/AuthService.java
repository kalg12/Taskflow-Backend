package com.taskflow.userservice.service;

import com.taskflow.userservice.config.JwtService;
import com.taskflow.userservice.dto.*;
import com.taskflow.userservice.model.Role;
import com.taskflow.userservice.model.User;
import com.taskflow.userservice.repository.RoleRepository;
import com.taskflow.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /*
     * register(request)
     * - Creates a new user with the default role USER.
     * - Password is hashed using the configured PasswordEncoder (BCrypt).
     * - Throws an exception if the email is already registered.
     *
     * English tip: think of registration as "create account". We never
     * store raw passwords — we hash them before saving.
     */
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already registered");

        Role roleUser = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "USER")));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(roleUser))
                .build();

        return userRepository.save(user);
    }

    /*
     * login(request)
     * - Authenticates user using AuthenticationManager (which checks email/password).
     * - If authentication succeeds, generate a JWT with the user's email as subject.
     * - Returns an AuthResponse containing the token.
     *
     * Casual example: user shows username/password to the system. If OK, the
     * system gives a token (a temporary key) to use in future requests.
     */
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var token = jwtService.generateToken(user.getEmail());
        return AuthResponse.builder().token(token).build();
    }
}
