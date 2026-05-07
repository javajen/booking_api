package com.jenwright.booking.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for user registration.
 *
 * This class represents the client request to create a new user account with full name,
 * email, and password. All fields are validated according to specified constraints.
 *
 * @author jen
 * @version 1.0
 */
@Data
public class RegisterRequest {
    /**
     * The full name of the user registering.
     * Must not be blank.
     */
    @NotBlank private String fullName;

    /**
     * The email address for the new user account.
     * Must be a valid email format and not blank.
     */
    @Email @NotBlank private String email;

    /**
     * The password for the new user account.
     * Must be at least 6 characters long.
     */
    @Size(min = 6) private String password;
}
