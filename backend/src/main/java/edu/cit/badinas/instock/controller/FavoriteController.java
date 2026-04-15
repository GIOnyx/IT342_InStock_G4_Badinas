package edu.cit.badinas.instock.controller;

import edu.cit.badinas.instock.dto.ApiResponse;
import edu.cit.badinas.instock.dto.FavoriteRecipeRequest;
import edu.cit.badinas.instock.entity.FavoriteRecipe;
import edu.cit.badinas.instock.entity.User;
import edu.cit.badinas.instock.repository.FavoriteRecipeRepository;
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
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRecipeRepository favoriteRecipeRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFavorites(
            @AuthenticationPrincipal User user) {

        List<Map<String, Object>> data = favoriteRecipeRepository.findByUserOrderBySavedAtDesc(user)
                .stream()
                .map(this::toMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Favorites loaded", data));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addFavorite(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FavoriteRecipeRequest request) {

        if (favoriteRecipeRepository.existsByUserAndExternalRecipeId(user, request.getExternalRecipeId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Recipe already in favorites", "FAVORITE-001", null));
        }

        FavoriteRecipe favoriteRecipe = new FavoriteRecipe();
        favoriteRecipe.setUser(user);
        favoriteRecipe.setExternalRecipeId(request.getExternalRecipeId());
        favoriteRecipe.setTitle(request.getTitle().trim());
        favoriteRecipe.setImageUrl(request.getImageUrl());
        favoriteRecipe.setSummary(request.getSummary());
        favoriteRecipe.setLikes(request.getLikes() == null ? 0 : request.getLikes());

        FavoriteRecipe saved = favoriteRecipeRepository.save(favoriteRecipe);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recipe added to favorites", toMap(saved)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        FavoriteRecipe favoriteRecipe = favoriteRecipeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Favorite recipe not found"));

        favoriteRecipeRepository.delete(favoriteRecipe);
        return ResponseEntity.ok(ApiResponse.success("Recipe removed from favorites", null));
    }

    private Map<String, Object> toMap(FavoriteRecipe recipe) {
        return Map.of(
                "id", recipe.getId(),
                "externalRecipeId", recipe.getExternalRecipeId(),
                "title", recipe.getTitle(),
                "imageUrl", recipe.getImageUrl() == null ? "" : recipe.getImageUrl(),
                "summary", recipe.getSummary() == null ? "" : recipe.getSummary(),
                "likes", recipe.getLikes() == null ? 0 : recipe.getLikes(),
                "savedAt", recipe.getSavedAt()
        );
    }
}
