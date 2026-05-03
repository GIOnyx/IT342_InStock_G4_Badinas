package edu.cit.badinas.instock.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication response DTO — Design Pattern: Builder (Manual Implementation).
 *
 * A static inner {@code AuthResponseBuilder} demonstrates the Builder pattern
 * for constructing response objects with many optional fields (e.g. avatarUrl
 * may be null for password-registered users) without telescoping constructors.
 */
@Data
@NoArgsConstructor
public class AuthResponse {

    private Long id;
    private String email;
    private String fullName;
    private String role;
    private String avatarUrl;
    private String token;

    // ── Builder Pattern (Manual Implementation) ───────────────────────

    /**
     * Entry point for the Builder pattern.
     */
    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    /**
     * Static inner Builder class that constructs {@link AuthResponse} instances
     * via a fluent, chainable API.
     */
    public static class AuthResponseBuilder {
        private Long id;
        private String email;
        private String fullName;
        private String role;
        private String avatarUrl;
        private String token;

        public AuthResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AuthResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public AuthResponseBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * Terminal operation — constructs and returns the fully-configured
         * {@link AuthResponse} instance.
         */
        public AuthResponse build() {
            AuthResponse response = new AuthResponse();
            response.setId(this.id);
            response.setEmail(this.email);
            response.setFullName(this.fullName);
            response.setRole(this.role);
            response.setAvatarUrl(this.avatarUrl);
            response.setToken(this.token);
            return response;
        }
    }
}