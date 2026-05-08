package edu.cit.badinas.instock.auth.security.strategy;

import edu.cit.badinas.instock.auth.AuthResponse;
import edu.cit.badinas.instock.auth.AuthenticationResult;
import edu.cit.badinas.instock.auth.LoginRequest;
import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.users.User;
import edu.cit.badinas.instock.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Design Pattern: Strategy — Standard Email/Password Authentication.
 *
 * This concrete strategy handles the traditional login flow:
 * <ol>
 *   <li>Look up the user by email in the database</li>
 *   <li>Verify the supplied password against the stored hash</li>
 *   <li>Generate a JWT on success</li>
 * </ol>
 * No new user is ever created during standard login, so
 * {@code newlyRegistered} is always {@code false}.
 */
@Component
@RequiredArgsConstructor
public class StandardEmailAuthStrategy implements AuthenticationStrategy {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthenticationResult authenticate(LoginRequest request) {
        // ── Input validation ──────────────────────────────────────────
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required for password authentication");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password is required for password authentication");
        }

        // ── Lookup & verify ───────────────────────────────────────────
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // ── Build response (Builder Pattern) ──────────────────────────
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        AuthResponse response = AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .token(token)
                .build();

        return new AuthenticationResult(response, false);
    }
}