package edu.cit.badinas.instock.auth;

import edu.cit.badinas.instock.core.dto.ApiResponse;
import edu.cit.badinas.instock.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 *
 * <h3>Design Pattern: Facade</h3>
 * This controller depends <b>only</b> on {@link AuthService}, which
 * acts as the Facade.  It has <em>zero</em> references to lower-level
 * components such as {@code UserRepository}, {@code JwtService},
 * {@code PasswordEncoder}, or authentication strategies.
 *
 * <p>Adding new post-registration behaviours (e.g. sending a welcome
 * email) requires no changes here — the Observer pattern handles that
 * inside the Facade.</p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;   // Facade — single dependency

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AuthResponse>> me(
            @AuthenticationPrincipal User user) {
        AuthResponse response = authService.getCurrentUser(user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Current user retrieved", response));
    }
}