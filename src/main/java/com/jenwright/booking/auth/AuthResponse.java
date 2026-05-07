package com.jenwright.booking.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for authentication operations.
 *
 * Contains the JWT token returned after successful user registration or login.
 * The token should be included in the Authorization header for subsequent
 * authenticated API requests.
 *
 * @author jen
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    /**
     * The JWT authentication token.
     *
     * This token is used for authenticating subsequent API requests by including
     * it in the Authorization header with the "Bearer" prefix.
     */
    private String token;
}
