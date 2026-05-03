package edu.cit.badinas.instock.pantry;

import edu.cit.badinas.instock.core.dto.ApiResponse;
import edu.cit.badinas.instock.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class PantryController {

    private final PantryItemRepository pantryItemRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPantry(
            @AuthenticationPrincipal User user) {

        List<Map<String, Object>> data = pantryItemRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Pantry loaded", data));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addPantryItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PantryItemRequest request) {

        String normalizedName = request.getName().trim();

        if (pantryItemRepository.existsByUserAndNameIgnoreCase(user, normalizedName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Ingredient already exists in pantry", "STOCK-001", null));
        }

        PantryItem pantryItem = new PantryItem();
        pantryItem.setUser(user);
        pantryItem.setName(normalizedName);

        PantryItem saved = pantryItemRepository.save(pantryItem);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ingredient added to pantry", toMap(saved)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePantryItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody PantryItemRequest request) {

        PantryItem pantryItem = pantryItemRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Pantry item not found"));

        String normalizedName = request.getName().trim();
        boolean isRenaming = !pantryItem.getName().equalsIgnoreCase(normalizedName);

        if (isRenaming && pantryItemRepository.existsByUserAndNameIgnoreCase(user, normalizedName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Ingredient already exists in pantry", "STOCK-001", null));
        }

        pantryItem.setName(normalizedName);

        PantryItem saved = pantryItemRepository.save(pantryItem);
        return ResponseEntity.ok(ApiResponse.success("Ingredient updated", toMap(saved)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deletePantryItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        PantryItem pantryItem = pantryItemRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Pantry item not found"));

        pantryItemRepository.delete(pantryItem);
        return ResponseEntity.ok(ApiResponse.success("Ingredient removed from pantry", null));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> clearPantry(
            @AuthenticationPrincipal User user) {

        long removedCount = pantryItemRepository.deleteByUser(user);
        return ResponseEntity.ok(ApiResponse.success("Removed " + removedCount + " pantry item(s)", null));
    }

    private Map<String, Object> toMap(PantryItem pantryItem) {
        return Map.of(
                "id", pantryItem.getId(),
                "name", pantryItem.getName(),
                "createdAt", pantryItem.getCreatedAt()
        );
    }
}
