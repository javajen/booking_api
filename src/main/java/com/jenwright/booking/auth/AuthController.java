package com.jenwright.booking.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling authentication operations.
 *
 * Provides endpoints for user registration and login. These endpoints are publicly
 * accessible and do not require authentication. Upon successful login, a JWT token
 * is returned for use in subsequent authenticated requests.
 *
 * @author jen
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user account.
     *
     * Creates a new user account with the provided registration details. The user
     * will be able to login immediately after successful registration.
     *
     * @param request the registration request containing user details
     * @return ResponseEntity containing the authentication response with JWT token
     * @throws IllegalArgumentException if the username is already taken or validation fails
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * Validates the provided credentials and returns a JWT token upon successful
     * authentication. The token should be included in the Authorization header
     * for subsequent authenticated requests.
     *
     * @param request the login request containing username and password
     * @return ResponseEntity containing the authentication response with JWT token
     * @throws IllegalArgumentException if the credentials are invalid
     */
    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
