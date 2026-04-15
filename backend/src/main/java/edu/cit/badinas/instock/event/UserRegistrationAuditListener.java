package edu.cit.badinas.instock.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Design Pattern: Observer — User Registration Audit Listener.
 *
 * This observer listens for {@link UserRegisteredEvent} instances
 * published by the Subject ({@code AuthService}).  It is completely
 * decoupled from the registration logic — {@code AuthService} has
 * no reference to this class.
 *
 * Currently logs a success message; additional observers (e.g. an
 * {@code EmailNotificationListener}) can be added independently
 * without modifying the Subject.
 */
@Component
public class UserRegistrationAuditListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationAuditListener.class);

    /**
     * Reacts to a new user registration by logging an audit entry.
     *
     * @param event the published registration event
     */
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info(">>> [OBSERVER] New user registered — email: {}, name: {}",
                event.getEmail(), event.getFullName());
    }
}
