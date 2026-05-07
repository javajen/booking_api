package com.jenwright.booking.config;

import com.jenwright.booking.auth.JwtAuthFilter;
import com.jenwright.booking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for JWT-based authentication.
 *
 * Configures the security filter chain with stateless JWT authentication, CSRF protection
 * disabled, and method-level security enabled. This configuration sets up public access to
 * authentication endpoints and Swagger UI while requiring authentication for all other endpoints.
 *
 * @author jen
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    /**
     * Provides the UserDetailsService for loading user information.
     *
     * Implements UserDetailsService by looking up users in the database
     * by email address. Throws UsernameNotFoundException if the user
     * does not exist.
     *
     * @return UserDetailsService implementation that loads users by email
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Configures the password encoder.
     *
     * Provides a BCryptPasswordEncoder for securely hashing user passwords.
     * BCrypt automatically handles salt generation and is the recommended
     * approach for password storage.
     *
     * @return PasswordEncoder using BCrypt algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication provider.
     *
     * Sets up a DAO authentication provider with the custom user details
     * service and password encoder for validating user credentials.
     *
     * @return AuthenticationProvider configured with user details and password encoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Provides the AuthenticationManager bean.
     *
     * Retrieves the default AuthenticationManager from Spring's authentication
     * configuration for use in the application's authentication logic.
     *
     * @param config the AuthenticationConfiguration to get the manager from
     * @return the AuthenticationManager
     * @throws Exception if authentication manager retrieval fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the HTTP security filter chain.
     *
     * Sets up the following security rules:
     * - CSRF protection disabled (stateless API doesn't need it)
     * - Public access to authentication endpoints (/api/auth/**)
     * - Public access to Swagger UI documentation
     * - Stateless session management for JWT authentication
     * - JWT filter added before the default authentication filter
     *
     * @param http the HttpSecurity object for configuring security
     * @param jwtAuthFilter the JWT authentication filter
     * @return the built SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}