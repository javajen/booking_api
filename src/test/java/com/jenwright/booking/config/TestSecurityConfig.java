package com.jenwright.booking.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test configuration for Spring Security.
 *
 * Provides a simplified security configuration for integration tests and test slices.
 * This configuration disables CSRF protection for testing, allows public access to
 * authentication endpoints, and requires authentication for all other endpoints.
 *
 * This configuration is used as a test-scoped alternative to the main SecurityConfig
 * to ensure tests can properly validate security constraints without full JWT
 * authentication flow complexity.
 *
 * @author jen
 * @version 1.0
 */
@TestConfiguration
public class TestSecurityConfig {

    /**
     * Configures the test HTTP security filter chain.
     *
     * Sets up security rules for testing:
     * - CSRF protection disabled for testing convenience
     * - Public access to authentication endpoints (/api/auth/**)
     * - Authentication required for all other endpoints
     *
     * @param http the HttpSecurity object for configuring security
     * @return the built SecurityFilterChain for testing
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }
}