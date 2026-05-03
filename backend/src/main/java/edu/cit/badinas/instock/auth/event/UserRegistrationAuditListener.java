package edu.cit.badinas.instock.auth.event;

import edu.cit.badinas.instock.core.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Design Pattern: Observer - User Registration Audit Listener.
 *
 * This observer listens for {@link UserRegisteredEvent} instances
 * published by the Subject ({@code AuthService}). It remains fully
 * decoupled from registration logic and can trigger side effects such
 * as email dispatch without changing the publisher.
 */
@Component
@RequiredArgsConstructor
public class UserRegistrationAuditListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationAuditListener.class);

    private final EmailService emailService;

    /**
     * Reacts to a new user registration by logging an audit entry and
     * dispatching a welcome email.
     *
     * @param event the published registration event
     */
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info(">>> [OBSERVER] New user registered - email: {}, name: {}",
                event.getEmail(), event.getFullName());

        try {
            emailService.sendWelcomeEmail(event.getEmail());
            log.info(">>> [OBSERVER] Welcome email dispatched to {}", event.getEmail());
        } catch (Exception ex) {
            log.error(">>> [OBSERVER] Failed to dispatch welcome email to {}", event.getEmail(), ex);
        }
    }
}
