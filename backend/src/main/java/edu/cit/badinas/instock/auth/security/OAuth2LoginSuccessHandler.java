package edu.cit.badinas.instock.auth.security;

import edu.cit.badinas.instock.auth.AuthResponse;
import edu.cit.badinas.instock.auth.AuthService;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Handles successful OAuth2 login redirects.
 *
 * <h3>Design Pattern: Facade</h3>
 * This handler is now thin — it extracts the OAuth2 user attributes
 * from Spring Security and immediately delegates all complex logic
 * (user lookup/creation, JWT generation, event publishing) to
 * {@link AuthService#authenticateOAuthUser}, which acts as the Facade.
 *
 * <p>Previously this class directly accessed {@code UserRepository}
 * and {@code JwtService}; those dependencies have been removed.</p>
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final AuthService authService;   // Facade — single dependency

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            response.sendRedirect(frontendUrl);
            return;
        }

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String email   = oauthUser.getAttribute("email");
        String name    = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        Assert.notNull(email, "OAuth2 provider did not supply an email");

        // ── Facade Pattern — delegate everything to AuthService ───────
        AuthResponse authResponse = authService.authenticateOAuthUser(email, name, picture);

        String redirect = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/login")
                .queryParam("token", authResponse.getToken())
                .build().toUriString();

        response.sendRedirect(redirect);
    }
}