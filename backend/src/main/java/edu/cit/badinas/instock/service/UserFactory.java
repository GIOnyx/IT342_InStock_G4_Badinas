package edu.cit.badinas.instock.service;

import edu.cit.badinas.instock.dto.RegisterRequest;
import edu.cit.badinas.instock.entity.Role;
import edu.cit.badinas.instock.entity.User;
import org.springframework.stereotype.Component;

/**
 * Design Pattern: Factory Method — User Entity Factory.
 *
 * Centralises the creation logic for {@link User} entities.  Instead of
 * scattering {@code User.builder()...build()} calls with varying field
 * combinations across services, all construction goes through this
 * factory, which ensures correct defaults (role, verification status)
 * are applied consistently.
 *
 * <ul>
 *   <li>{@link #createStandardUser} — for email/password registration</li>
 *   <li>{@link #createOAuthUser}    — for first-time Google OAuth login</li>
 * </ul>
 */
@Component
public class UserFactory {

    /**
     * Factory method for standard (email + password) registration.
     *
     * @param request         the registration form data
     * @param encodedPassword the already-hashed password
     * @return a new, unsaved {@link User} with role USER and unverified status
     */
    public User createStandardUser(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .fullName(request.getFullName())
                .role(Role.USER)
                .isVerified(false)
                .build();
    }

    /**
     * Factory method for first-time OAuth (Google) login.
     *
     * @param email     the email address from the OAuth provider
     * @param fullName  the display name (falls back to email if null)
     * @param avatarUrl the profile picture URL (nullable)
     * @return a new, unsaved {@link User} with role USER and verified status
     */
    public User createOAuthUser(String email, String fullName, String avatarUrl) {
        return User.builder()
                .email(email)
                .fullName(fullName != null ? fullName : email)
                .avatarUrl(avatarUrl)
                .role(Role.USER)
                .isVerified(true)   // OAuth users are pre-verified by the provider
                .build();
    }
}
