package edu.cit.badinas.instock.auth.security.strategy;

import edu.cit.badinas.instock.auth.AuthenticationResult;
import edu.cit.badinas.instock.auth.LoginRequest;

/**
 * Design Pattern: Strategy — Authentication Strategy interface.
 *
 * Defines a family of authentication algorithms (e.g. email/password,
 * Google OAuth) and makes them interchangeable.  The concrete strategy
 * is selected at runtime by the context ({@code AuthService}) based on
 * the {@code authType} field of the incoming {@link LoginRequest}.
 */
public interface AuthenticationStrategy {

    /**
     * Authenticate the user described by the given request.
     *
     * @param request the login request containing credentials
     * @return an {@link AuthenticationResult} wrapping the response and
     *         a flag indicating whether a new user was created
     * @throws RuntimeException if authentication fails
     */
    AuthenticationResult authenticate(LoginRequest request);
}