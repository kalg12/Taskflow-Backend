package com.taskflow.userservice.config;

import com.taskflow.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/*
 * JWT authentication filter.
 *
 * - This filter runs for each HTTP request.
 * - It looks for an Authorization header with a Bearer token.
 * - If a token is present, it delegates to JwtService to extract the email
 *   (the token subject) and validate the token signature and expiration.
 * - If the token is valid and a matching user is found in the database,
 *   the filter builds a UserDetails object with granted authorities and
 *   places an Authentication into the SecurityContext. This makes Spring
 *   treat the request as authenticated for downstream controllers.
 *
 * Casual example: think of this filter as a security guard at the door who
 * checks the "token" ID card, confirms it's valid, and then tells the app
 * who the user is and what roles they have.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String userEmail = jwtService.extractEmail(token);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userEntity = userRepository.findByEmail(userEmail);
            if (userEntity.isPresent() && jwtService.isTokenValid(token, userEmail)) {
                var roles = userEntity.get().getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                        .collect(Collectors.toList());

                UserDetails userDetails = new User(userEmail, userEntity.get().getPassword(), roles);
                var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, roles);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}
