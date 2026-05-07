package com.jenwright.booking.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jenwright.booking.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController.
 *
 * Tests the authentication endpoints for user registration and login with comprehensive
 * coverage of both happy path scenarios and edge cases including validation failures,
 * error conditions, and security constraints. Uses MockMvc for testing REST endpoints
 * and Mockito for mocking service dependencies.
 *
 * Test coverage includes:
 * - Successful user registration and login
 * - Validation of request fields (email format, password length)
 * - Duplicate email rejection
 * - Invalid credentials handling
 * - Public access verification for auth endpoints
 *
 * @author jen
 * @version 1.0
 */
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    /**
     * MockMvc instance for performing HTTP requests and verifying responses.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper for serializing request objects to JSON.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mock AuthService dependency.
     */
    @MockBean
    private AuthService authService;

    /**
     * Mock JwtService dependency.
     */
    @MockBean
    private JwtService jwtService;

    /**
     * Mock UserDetailsService dependency.
     */
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("should register new user successfully with valid request")
    void shouldRegisterNewUserWithValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("New User");
        request.setEmail("newuser@test.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("jwt-token-123");
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    @DisplayName("should return bad request when registration request is invalid")
    void shouldReturnBadRequestForInvalidRegistrationRequest() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setFullName("");
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should throw exception when email is already registered")
    void shouldThrowExceptionWhenEmailAlreadyRegistered() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("User");
        request.setEmail("existing@test.com");
        request.setPassword("password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already in use"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should not require authentication for register endpoint")
    void shouldAllowPublicAccessToRegisterEndpoint() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Public User");
        request.setEmail("public@test.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("jwt-token-456");
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should login user successfully with valid credentials")
    void shouldLoginUserWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("jwt-token-789");
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-789"));
    }

    @Test
    @DisplayName("should throw exception when login credentials are invalid")
    void shouldThrowExceptionWhenCredentialsAreInvalid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return bad request when login request is invalid")
    void shouldReturnBadRequestForInvalidLoginRequest() throws Exception {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should not require authentication for login endpoint")
    void shouldAllowPublicAccessToLoginEndpoint() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("jwt-token-public");
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should throw exception when user not found during login")
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@test.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should validate email format in registration request")
    void shouldValidateEmailFormatInRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("User");
        request.setEmail("not-an-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should validate email format in login request")
    void shouldValidateEmailFormatInLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("not-an-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should validate password minimum length in registration")
    void shouldValidatePasswordMinimumLengthInRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("User");
        request.setEmail("user@test.com");
        request.setPassword("12345");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

