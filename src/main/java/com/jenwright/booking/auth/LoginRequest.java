package com.jenwright.booking.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for user login.
 *
 * This class represents the client request to authenticate a user with email and password credentials.
 * Both fields are required and validated.
 *
 * @author jen
 * @version 1.0
 */
@Data
public class LoginRequest {
    /**
     * The email address of the user attempting to log in.
     * Must be a valid email format and not blank.
     */
    @Email @NotBlank private String email;

    /**
     * The user's password.
     * Must not be blank.
     */
    @NotBlank private String password;
}
