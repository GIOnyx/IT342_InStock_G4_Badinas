package edu.cit.badinas.instock.security;

import edu.cit.badinas.instock.entity.Role;
import edu.cit.badinas.instock.entity.User;
import edu.cit.badinas.instock.repository.UserRepository;
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

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            response.sendRedirect(frontendUrl);
            return;
        }

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        Assert.notNull(email, "OAuth2 provider did not supply an email");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = User.builder()
                    .email(email)
                    .fullName(name != null ? name : email)
                    .avatarUrl(picture)
                    .role(Role.USER)
                    .isVerified(true)
                    .build();
            return userRepository.save(u);
        });

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        String redirect = UriComponentsBuilder.fromUriString(frontendUrl)
            .path("/login")
                .queryParam("token", token)
                .build().toUriString();

        response.sendRedirect(redirect);
    }
}
