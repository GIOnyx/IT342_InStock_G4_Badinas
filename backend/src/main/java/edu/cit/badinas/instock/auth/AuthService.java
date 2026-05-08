package edu.cit.badinas.instock.auth;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.cit.badinas.instock.auth.event.UserRegisteredEvent;
import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.auth.security.strategy.AuthenticationStrategy;
import edu.cit.badinas.instock.auth.security.strategy.GoogleOAuthStrategy;
import edu.cit.badinas.instock.auth.security.strategy.StandardEmailAuthStrategy;
import edu.cit.badinas.instock.users.User;
import edu.cit.badinas.instock.users.UserFactory;
import edu.cit.badinas.instock.users.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * AuthService — the central orchestrator of the authentication subsystem.
 *
 * <h3>Design Patterns applied</h3>
 * <ul>
 *   <li><b>Facade</b> (Structural) — {@code AuthController} calls only this
 *       class.  All lower-level components (UserRepository, JwtService,
 *       PasswordEncoder, strategies, event publisher) are hidden behind
 *       simple methods like {@link #register} and {@link #login}.</li>
 *
 *   <li><b>Strategy</b> (Behavioral) — {@link #login} delegates to the
 *       appropriate {@link AuthenticationStrategy} resolved at runtime by
 *       {@link #resolveStrategy}.  New login methods (e.g. Apple Sign-In)
 *       can be added by creating a new Strategy class without modifying
 *       this service.</li>
 *
 *   <li><b>Factory Method</b> (Creational) — User entity construction is
 *       delegated to {@link UserFactory} so that default values (role,
 *       verification status) are set consistently.</li>
 *
 *   <li><b>Observer</b> (Behavioral) — After a user is created (either by
 *       registration or first-time OAuth), a {@link UserRegisteredEvent}
 *       is published via Spring's {@code ApplicationEventPublisher}.
 *       Listeners such as {@code UserRegistrationAuditListener} react
 *       without this class knowing about them.</li>
 *
 *   <li><b>Builder</b> (Creational) — {@link AuthResponse} and
 *       {@link User} objects are constructed via their manual Builder
 *       inner classes.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    // ── Dependencies (hidden from AuthController — Facade) ────────────
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserFactory userFactory;                         // Factory Method
    private final ApplicationEventPublisher eventPublisher;        // Observer

    // Strategy Pattern: injectable concrete strategies
    private final StandardEmailAuthStrategy standardEmailAuthStrategy;
    private final GoogleOAuthStrategy googleOAuthStrategy;

    // ══════════════════════════════════════════════════════════════════
    //  Facade Method: Register (email + password)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Registers a new user with email and password.
     *
     * @param request the registration form data
     * @return an {@link AuthResponse} containing the JWT and user info
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Factory Method Pattern — delegate User creation to UserFactory
        User user = userFactory.createStandardUser(
                request, passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        // Observer Pattern — publish registration event
        eventPublisher.publishEvent(
                new UserRegisteredEvent(this, user.getEmail(), user.getFullName()));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        // Builder Pattern — construct response with fluent API
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .token(token)
                .build();
    }

    // ══════════════════════════════════════════════════════════════════
    //  Facade Method: Login (Strategy Context)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Authenticates a user by delegating to the correct
     * {@link AuthenticationStrategy} based on the request's
     * {@code authType} field.
     *
     * @param request the login request (email+password OR Google token)
     * @return an {@link AuthResponse} containing the JWT and user info
     */
    public AuthResponse login(LoginRequest request) {
        // Strategy Pattern — resolve and delegate
        AuthenticationStrategy strategy = resolveStrategy(request.getAuthType());
        AuthenticationResult result = strategy.authenticate(request);

        // Observer Pattern — publish event if a new user was created
        //                    (e.g. first-time Google OAuth login)
        if (result.isNewlyRegistered()) {
            AuthResponse resp = result.getAuthResponse();
            eventPublisher.publishEvent(
                    new UserRegisteredEvent(this, resp.getEmail(), resp.getFullName()));
        }

        return result.getAuthResponse();
    }

    // ══════════════════════════════════════════════════════════════════
    //  Facade Method: OAuth2 redirect-flow (used by SuccessHandler)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Handles the server-side OAuth2 redirect flow.  Called by
     * {@code OAuth2LoginSuccessHandler} after Spring Security has
     * validated the OAuth2 token.
     *
     * @param email   email from the OAuth2 provider
     * @param name    display name (nullable)
     * @param picture avatar URL (nullable)
     * @return an {@link AuthResponse} containing the JWT and user info
     */
    public AuthResponse authenticateOAuthUser(String email, String name, String picture) {
        AtomicBoolean isNew = new AtomicBoolean(false);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            isNew.set(true);
            // Factory Method Pattern — delegate User creation to UserFactory
            User newUser = userFactory.createOAuthUser(email, name, picture);
            return userRepository.save(newUser);
        });

        // Observer Pattern — publish event for first-time OAuth users
        if (isNew.get()) {
            eventPublisher.publishEvent(
                    new UserRegisteredEvent(this, user.getEmail(), user.getFullName()));
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        // Builder Pattern — construct response with fluent API
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .token(token)
                .build();
    }

    /**
     * Returns the current authenticated user's profile details.
     *
     * @param email authenticated user's email
     * @return profile information for the authenticated user
     */
    public AuthResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    // ══════════════════════════════════════════════════════════════════
    //  Strategy Resolution
    // ══════════════════════════════════════════════════════════════════

    /**
     * Selects the concrete {@link AuthenticationStrategy} for the
     * given authentication type.
     *
     * @param authType "password" or "google"
     * @return the matching strategy
     */
    private AuthenticationStrategy resolveStrategy(String authType) {
        if ("google".equalsIgnoreCase(authType)) {
            return googleOAuthStrategy;
        }
        return standardEmailAuthStrategy; // default
    }
}