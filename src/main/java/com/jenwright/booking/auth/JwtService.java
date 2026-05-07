package com.jenwright.booking.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT token operations.
 *
 * Handles JWT token generation, validation, and claim extraction. This service uses
 * JJWT (JSON Web Token) library for token operations and supports configurable
 * secret keys and expiration times loaded from application properties.
 *
 * @author jen
 * @version 1.0
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token the JWT token string
     * @return the username/email stored as the token subject
     * @throws JwtException if the token is invalid or expired
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT token using a custom resolver.
     *
     * This is a generic method that allows extraction of any claim type from the token.
     *
     * @param <T> the type of the claim value
     * @param token the JWT token string
     * @param claimsResolver a function that extracts the desired claim from the token's claims
     * @return the extracted claim value
     * @throws JwtException if the token is invalid or expired
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for a user without additional claims.
     *
     * Creates a token with the user's username as the subject and default expiration time.
     *
     * @param userDetails the user details containing username and authorities
     * @return the generated JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token for a user with additional custom claims.
     *
     * Creates a token with the specified extra claims, user's username as subject,
     * issued at time, and expiration time configured via properties.
     *
     * @param extraClaims a map of additional claims to include in the token
     * @param userDetails the user details containing username and authorities
     * @return the generated JWT token string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT token against user details.
     *
     * Checks that the token's username matches the provided user details
     * and that the token has not expired.
     *
     * @param token the JWT token string to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid and matches the user, false otherwise
     * @throws JwtException if the token signature or format is invalid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token the JWT token string
     * @return true if the token has expired, false otherwise
     * @throws JwtException if the token is invalid
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token string
     * @return the expiration date of the token
     * @throws JwtException if the token is invalid
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * Parses the token using the signing key and validates the signature.
     *
     * @param token the JWT token string
     * @return the Claims object containing all token claims
     * @throws JwtException if the token signature is invalid or parsing fails
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates the signing key from the configured secret.
     *
     * Converts the secret key string to bytes and creates an HMAC SHA key
     * for signing and validating JWT tokens.
     *
     * @return the Key object used for JWT signing
     */
    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
