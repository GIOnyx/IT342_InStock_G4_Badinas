package edu.cit.badinas.instock.core.config;

import edu.cit.badinas.instock.users.User;
import edu.cit.badinas.instock.users.UserRepository;
import edu.cit.badinas.instock.users.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * On application startup, ensure an admin user exists.
 * Default credentials can be overridden via environment variables:
 *  - ADMIN_EMAIL (default: admin@instock.local)
 *  - ADMIN_PASSWORD (default: ChangeMe123!)
 */
@Component
@Profile("!test")
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:admin@instock.local}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:ChangeMe123!}")
    private String adminPassword;

    public AdminAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User admin = User.builder()
                .email(adminEmail)
                .fullName("Administrator")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .isVerified(true)
                .build();

        userRepository.save(admin);
    }
}
