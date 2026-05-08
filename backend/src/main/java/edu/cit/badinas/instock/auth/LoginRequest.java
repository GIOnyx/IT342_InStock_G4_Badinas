package edu.cit.badinas.instock.auth;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * Login request DTO — extended to support the Strategy Pattern.
 *
 * {@code authType} determines which {@code AuthenticationStrategy} is used:
 * <ul>
 *   <li>{@code "password"} (default) — standard email + password login</li>
 *   <li>{@code "google"}  — Google OAuth2 ID-token login</li>
 * </ul>
 */
@Data
public class LoginRequest {

    @Email(message = "Invalid email format")
    private String email;

    private String password;

    /** Authentication type: "password" (default) or "google". */
    private String authType = "password";

    /** Google ID token — required when authType is "google". */
    private String idToken;
}