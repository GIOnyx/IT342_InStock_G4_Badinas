package edu.cit.badinas.instock.auth.security.strategy;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import edu.cit.badinas.instock.auth.AuthResponse;
import edu.cit.badinas.instock.auth.AuthenticationResult;
import edu.cit.badinas.instock.auth.LoginRequest;
import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.users.User;
import edu.cit.badinas.instock.users.UserFactory;
import edu.cit.badinas.instock.users.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Design Pattern: Strategy — Google OAuth2 ID-Token Authentication.
 *
 * This concrete strategy handles token-based Google login (typically
 * used by mobile or SPA clients that already possess a Google ID token):
 * <ol>
 *   <li>Validate the ID token via Google's {@code tokeninfo} endpoint</li>
 *   <li>Extract user attributes (email, name, picture)</li>
 *   <li>Find or create the user in the database (Factory Method Pattern)</li>
 *   <li>Generate a JWT</li>
 * </ol>
 * {@code newlyRegistered} is {@code true} when a user is created for
 * the first time, allowing the Observer to fire a welcome event.
 */
@Component
@RequiredArgsConstructor
public class GoogleOAuthStrategy implements AuthenticationStrategy {

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final JwtService jwtService;

    private static final String GOOGLE_TOKEN_INFO_URL =
            "https://oauth2.googleapis.com/tokeninfo?id_token=";

    @Override
    public AuthenticationResult authenticate(LoginRequest request) {
        // ── Input validation ──────────────────────────────────────────
        if (request.getIdToken() == null || request.getIdToken().isBlank()) {
            throw new RuntimeException("Google ID token is required for Google authentication");
        }

        // ── Validate token with Google ────────────────────────────────
        Map<String, Object> tokenInfo = validateGoogleToken(request.getIdToken());

        String email   = (String) tokenInfo.get("email");
        String name    = (String) tokenInfo.getOrDefault("name", email);
        String picture = (String) tokenInfo.get("picture");

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Google token did not contain an email address");
        }

        // ── Find or create user (Factory Method Pattern) ──────────────
        AtomicBoolean isNew = new AtomicBoolean(false);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            isNew.set(true);
            User newUser = userFactory.createOAuthUser(email, name, picture);
            return userRepository.save(newUser);
        });

        // ── Build response (Builder Pattern) ──────────────────────────
        String jwt = jwtService.generateToken(user.getEmail(), user.getRole().name());

        AuthResponse response = AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .token(jwt)
                .build();

        return new AuthenticationResult(response, isNew.get());
    }

    // ── Private helper ────────────────────────────────────────────────

    /**
     * Calls Google's {@code tokeninfo} endpoint to verify the ID token
     * and returns the parsed payload as a Map.
     */
    private Map<String, Object> validateGoogleToken(String idToken) {
        try {
            RestClient restClient = RestClient.create();
            Map<String, Object> response = restClient.get()
                    .uri(GOOGLE_TOKEN_INFO_URL + idToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null || response.containsKey("error_description")) {
                throw new RuntimeException("Invalid Google ID token");
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate Google ID token: " + e.getMessage());
        }
    }
}