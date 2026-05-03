package edu.cit.badinas.instock.users;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity - Design Pattern: Builder (Manual Implementation).
 *
 * A static inner {@code UserBuilder} class provides a fluent API for
 * constructing User objects step-by-step, replacing telescoping constructors.
 * Default values for {@code role} (USER) and {@code isVerified} (false)
 * are set inside the builder so callers can omit them.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // -- Builder Pattern (Manual Implementation) --

    /**
     * Entry point for the Builder pattern - returns a new {@link UserBuilder}.
     */
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    /**
     * Static inner Builder class that constructs {@link User} instances
     * via a fluent, chainable API.  Each setter method returns {@code this}
     * so calls can be chained: {@code User.builder().email("a@b.com").fullName("A").build();}
     */
    public static class UserBuilder {
        private Long id;
        private String email;
        private String passwordHash;
        private String fullName;
        private Role role = Role.USER;          // sensible default
        private String avatarUrl;
        private LocalDateTime createdAt;
        private Boolean isVerified = false;     // sensible default

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public UserBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder isVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        /**
         * Terminal operation - constructs and returns the fully-configured
         * {@link User} instance.
         */
        public User build() {
            User user = new User();
            user.setId(this.id);
            user.setEmail(this.email);
            user.setPasswordHash(this.passwordHash);
            user.setFullName(this.fullName);
            user.setRole(this.role);
            user.setAvatarUrl(this.avatarUrl);
            user.setCreatedAt(this.createdAt);
            user.setIsVerified(this.isVerified);
            return user;
        }
    }
}