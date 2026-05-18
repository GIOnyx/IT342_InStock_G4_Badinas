package edu.cit.badinas.instock.users;

import edu.cit.badinas.instock.core.dto.ApiResponse;
import edu.cit.badinas.instock.favorites.FavoriteRecipeRepository;
import edu.cit.badinas.instock.pantry.PantryItemRepository;
import edu.cit.badinas.instock.users.dto.UserSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin-only controller.
 *
 * <p>All endpoints require the {@code ADMIN} role (enforced via
 * {@code @PreAuthorize}).  A regular user hitting any route here
 * receives {@code 403 Forbidden} — satisfying SDD AC-3 (RBAC).
 *
 * <p>SDD Journey 3 step 3: Admin views system usage statistics.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PantryItemRepository pantryItemRepository;
    private final FavoriteRecipeRepository favoriteRecipeRepository;

    /**
     * Returns aggregate platform statistics.
     *
     * <ul>
     *   <li>{@code totalUsers} — total registered users</li>
     *   <li>{@code totalPantryItems} — total pantry entries across all users</li>
     *   <li>{@code totalFavorites} — total saved recipes across all users</li>
     * </ul>
     *
     * @return {@link ApiResponse} wrapping a statistics map
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStats() {
        long totalUsers = userRepository.count();
        long totalPantryItems = pantryItemRepository.count();
        long totalFavorites = favoriteRecipeRepository.count();

        Map<String, Long> stats = Map.of(
                "totalUsers", totalUsers,
                "totalPantryItems", totalPantryItems,
                "totalFavorites", totalFavorites
        );

        return ResponseEntity.ok(ApiResponse.success("System statistics", stats));
    }

    /**
     * Returns a list of registered users with safe fields only.
     *
     * @return {@link ApiResponse} wrapping a list of user summaries
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserSummaryDTO>>> getUsers() {
        List<UserSummaryDTO> users = userRepository.findAll().stream()
                .map(user -> {
                    UserSummaryDTO dto = new UserSummaryDTO();
                    dto.setId(user.getId());
                    dto.setFullName(user.getFullName());
                    dto.setEmail(user.getEmail());
                    dto.setRole(user.getRole() != null ? user.getRole().name() : null);
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Registered users", users));
    }
}
