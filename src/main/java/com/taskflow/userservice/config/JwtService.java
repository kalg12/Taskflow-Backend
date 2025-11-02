package com.taskflow.userservice.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    /*
     * NOTE: This secret key is used to sign and verify JWTs. In production
     * you should NOT hardcode the key in source code. Instead, load it from a
     * secure location such as environment variables, a secrets manager, or
     * an encrypted configuration store.
     *
     * The key must have sufficient entropy/length for the chosen algorithm
     * (HS256 requires a key of adequate length, generally >= 256 bits).
     */
    private static final String SECRET_KEY = "SuperSecretKeyForJWTGenerationThatShouldBe32BytesLong!";

    /*
     * getSignInKey()
     * - Converts the raw secret bytes into a Key instance understood by the
     *   JJWT library. The Keys.hmacShaKeyFor(...) helper validates that the
     *   provided bytes are suitable for HMAC-based algorithms.
     * - Keep signing keys private and rotate them periodically.
     */
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /*
     * generateToken(email)
     * - Creates a compact JWT with a subject (email), issuedAt and expiration
     *   claims, and signs it using the HS256 algorithm and the signing key.
     * - The token is a self-contained way to carry authentication data (subject
     *   and other claims). Do not store sensitive data inside the JWT payload
     *   unless it is encrypted or strictly necessary.
     * - In this example expiration is set to 24 hours. Choose an expiration
     *   appropriate for your security requirements.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     * extractEmail(token)
     * - Convenience method that extracts the 'sub' (subject) claim which in
     *   this application is the user's email.
     * - It delegates to extractClaim(...), which parses and validates the JWT
     *   signature before reading claims. If the token is invalid or expired,
     *   parsing will throw a JwtException (e.g., ExpiredJwtException).
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /*
     * extractClaim(token, claimsResolver)
     * - Generic method to parse the JWT, validate its signature, and return
     *   a specific claim mapped by the provided function.
     * - Jwts.parserBuilder().setSigningKey(...).build().parseClaimsJws(token)
     *   will validate the signature and the token structure. If validation
     *   fails, the call throws a JwtException (subclasses include
     *   SignatureException, MalformedJwtException, ExpiredJwtException, etc.).
     * - Consider catching these exceptions at the boundary (e.g., in the
     *   authentication filter or controller) and returning clear HTTP statuses
     *   (401 Unauthorized or 403 Forbidden) without leaking internal details.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    /*
     * isTokenValid(token, userEmail)
     * - Checks two things:
     *   1) the token subject matches the expected user email
     *   2) the token is not expired
     * - This is a minimal validation. Depending on your needs you might also
     *   check additional claims (roles, issuer, audience, a token id for
     *   revocation, etc.).
     */
    public boolean isTokenValid(String token, String userEmail) {
        return userEmail.equals(extractEmail(token)) && !isTokenExpired(token);
    }

    /*
     * isTokenExpired(token)
     * - Reads the expiration claim and compares it with the current time.
     * - extractClaim(...) will throw if the token is malformed or the
     *   signature is invalid; catching those exceptions earlier is recommended.
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
