package edu.cit.badinas.instock.auth.event;

import org.springframework.context.ApplicationEvent;

/**
 * Design Pattern: Observer — User Registered Event.
 *
 * Published by the Subject ({@code AuthService}) whenever a new user is
 * successfully created, either through standard registration or first-time
 * OAuth login.  Observer classes annotated with {@code @EventListener}
 * receive this event without the publisher knowing about them, achieving
 * full decoupling between registration logic and post-registration actions.
 */
public class UserRegisteredEvent extends ApplicationEvent {

    private final String email;
    private final String fullName;

    /**
     * @param source   the component that published the event
     * @param email    the new user's email address
     * @param fullName the new user's display name
     */
    public UserRegisteredEvent(Object source, String email, String fullName) {
        super(source);
        this.email = email;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}