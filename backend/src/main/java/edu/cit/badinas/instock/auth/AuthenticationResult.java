package edu.cit.badinas.instock.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps an {@link AuthResponse} together with a flag indicating whether
 * the user was newly created during authentication (e.g. first-time OAuth login).
 *
 * Used internally by the Strategy pattern so the Facade (AuthService)
 * can decide whether to publish a registration event.
 */
@Data
@AllArgsConstructor
public class AuthenticationResult {

    /** The authentication response to return to the client. */
    private AuthResponse authResponse;

    /** {@code true} if a new User entity was created during this authentication. */
    private boolean newlyRegistered;
}