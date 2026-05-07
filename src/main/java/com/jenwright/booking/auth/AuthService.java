package com.jenwright.booking.auth;

import com.jenwright.booking.user.Role;
import com.jenwright.booking.user.User;
import com.jenwright.booking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication operations.
 *
 * Manages user registration, login, and JWT token generation. This service
 * handles password encoding, user validation, and authentication through
 * Spring Security's AuthenticationManager.
 *
 * @author jen
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user account.
     *
     * Creates a new user with the provided registration details, encodes the password,
     * assigns the CUSTOMER role, and generates a JWT token for immediate authentication.
     *
     * @param request the registration request containing user details
     * @return AuthResponse containing the JWT token for the new user
     * @throws IllegalArgumentException if the email is already registered
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        var user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);
        var token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * Validates the provided credentials using Spring Security's authentication manager,
     * then generates and returns a JWT token for the authenticated user.
     *
     * @param request the login request containing email and password
     * @return AuthResponse containing the JWT token for the authenticated user
     * @throws IllegalArgumentException if the user is not found or credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
