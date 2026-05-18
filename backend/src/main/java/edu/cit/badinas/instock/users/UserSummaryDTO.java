package edu.cit.badinas.instock.users;

/**
 * Password-safe DTO for the admin user list endpoint.
 *
 * <p>Intentionally omits {@code passwordHash} and other sensitive fields,
 * satisfying the SDD non-functional requirement: "DTOs used to prevent
 * sensitive data exposure."
 *
 * @param id         the user's primary key
 * @param fullName   the user's display name
 * @param email      the user's unique email address
 * @param role       the user's role (USER or ADMIN)
 * @param isVerified whether the user has verified their email
 */
public record UserSummaryDTO(
        Long id,
        String fullName,
        String email,
        String role,
        Boolean isVerified
) {
    /** Factory method that maps a {@link User} entity to this DTO. */
    public static UserSummaryDTO from(User user) {
        return new UserSummaryDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getIsVerified()
        );
    }
}
