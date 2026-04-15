package edu.cit.badinas.instock.dto.recipe;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Internal application DTO for recipe data — the "Target" interface in the
 * Adapter Pattern.
 *
 * <p>Uses clean, consistent field naming that is independent of any external
 * API.  If Spoonacular changes its JSON schema, only the
 * {@link edu.cit.badinas.instock.service.recipe.RecipeAdapter Adapter}
 * needs updating — all consumers of this DTO remain unchanged.</p>
 */
@Data
@NoArgsConstructor
public class RecipeDTO {

    /** Spoonacular recipe ID (useful for fetching full details later). */
    private Long recipeId;

    /** Human-readable recipe title. */
    private String title;

    /** URL of the recipe image. */
    private String imageUrl;

    /** Number of searched ingredients that this recipe uses. */
    private int matchedIngredientCount;

    /** Number of additional ingredients the user still needs. */
    private int missingIngredientCount;

    /** Names of searched ingredients that are used in this recipe. */
    private List<String> matchedIngredients;

    /** Names of ingredients the user is missing for this recipe. */
    private List<String> missingIngredients;

    /** Community popularity score. */
    private int likes;
}
