package com.jenwright.booking.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entity representing a user in the booking system.
 *
 * This entity implements Spring Security's UserDetails interface to enable authentication
 * and authorization. Users have an email, password, full name, and role that determines
 * their permissions within the system.
 *
 * @author jen
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The email address of the user (username).
     * Must be unique across all users and cannot be null.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The encrypted password for the user.
     * Must not be null. Passwords are hashed using BCrypt.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The full name of the user.
     * Must not be null.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * The role assigned to the user.
     * Determines the user's permissions and access level.
     * Must not be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Returns the authorities (permissions) granted to the user based on their role.
     *
     * Converts the user's role to a Spring Security GrantedAuthority by prefixing
     * with "ROLE_" to conform to Spring Security conventions.
     *
     * @return a collection containing a single GrantedAuthority based on the user's role
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Returns the username for authentication.
     *
     * @return the user's email address
     */
    @Override public String getUsername() { return email; }

    /**
     * Indicates whether the user's account has not expired.
     *
     * @return true as accounts do not expire in this application
     */
    @Override public boolean isAccountNonExpired() { return true; }

    /**
     * Indicates whether the user's account is not locked.
     *
     * @return true as accounts are not locked in this application
     */
    @Override public boolean isAccountNonLocked() { return true; }

    /**
     * Indicates whether the user's credentials (password) have not expired.
     *
     * @return true as credentials do not expire in this application
     */
    @Override public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indicates whether the user is enabled and active.
     *
     * @return true as users are enabled by default in this application
     */
    @Override public boolean isEnabled() { return true; }
}
