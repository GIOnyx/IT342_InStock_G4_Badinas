package edu.cit.badinas.instock.auth;

import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.auth.security.strategy.GoogleOAuthStrategy;
import edu.cit.badinas.instock.auth.security.strategy.StandardEmailAuthStrategy;
import edu.cit.badinas.instock.users.Role;
import edu.cit.badinas.instock.users.User;
import edu.cit.badinas.instock.users.UserFactory;
import edu.cit.badinas.instock.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserFactory userFactory;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private StandardEmailAuthStrategy standardEmailAuthStrategy;

    @Mock
    private GoogleOAuthStrategy googleOAuthStrategy;

    @Test
    void updateCurrentUserChangesFullNameOnly() {
        AuthService authService = authService();
        User user = user("old@test.com", "Old Name", "hash");
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("New Name");

        when(userRepository.findByEmail("old@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        AuthResponse response = authService.updateCurrentUser("old@test.com", request);

        assertThat(response.getFullName()).isEqualTo("New Name");
        assertThat(response.getEmail()).isEqualTo("old@test.com");
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRequiresCorrectCurrentPassword() {
        AuthService authService = authService();
        User user = user("user@test.com", "Test User", "old-hash");
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrong-password");
        request.setNewPassword("new-password");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "old-hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword("user@test.com", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Current password is incorrect");

        verify(userRepository, never()).save(user);
    }

    @Test
    void changePasswordSavesEncodedNewPassword() {
        AuthService authService = authService();
        User user = user("user@test.com", "Test User", "old-hash");
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("old-password");
        request.setNewPassword("new-password");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");
        when(userRepository.save(user)).thenReturn(user);

        AuthResponse response = authService.changePassword("user@test.com", request);

        assertThat(user.getPasswordHash()).isEqualTo("new-hash");
        assertThat(response.getFullName()).isEqualTo("Test User");
        verify(userRepository).save(user);
    }

    private AuthService authService() {
        return new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                userFactory,
                eventPublisher,
                standardEmailAuthStrategy,
                googleOAuthStrategy
        );
    }

    private User user(String email, String fullName, String passwordHash) {
        return User.builder()
                .id(1L)
                .email(email)
                .fullName(fullName)
                .passwordHash(passwordHash)
                .role(Role.USER)
                .build();
    }
}
