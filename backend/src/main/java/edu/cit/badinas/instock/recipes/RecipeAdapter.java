package edu.cit.badinas.instock.recipes;

import edu.cit.badinas.instock.recipes.dto.RecipeDTO;
import edu.cit.badinas.instock.recipes.dto.SpoonacularIngredientDTO;
import edu.cit.badinas.instock.recipes.dto.SpoonacularRecipeDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Design Pattern: Adapter (Structural).
 *
 * <p>This component acts as a <b>translator</b> between the external
 * Spoonacular API data format ({@link SpoonacularRecipeDTO}) and the
 * application's internal data format ({@link RecipeDTO}).</p>
 *
 * <h3>Why an Adapter?</h3>
 * <ul>
 *   <li>The external API uses field names like {@code usedIngredientCount},
 *       {@code missedIngredients}, and {@code image}.  The internal model
 *       uses {@code matchedIngredientCount}, {@code missingIngredients},
 *       and {@code imageUrl}.</li>
 *   <li>If Spoonacular changes its JSON schema or field naming in the
 *       future, <b>only this adapter</b> needs to be updated — the React
 *       web app and Android mobile app continue to receive the same
 *       consistent {@link RecipeDTO} structure.</li>
 * </ul>
 */
@Component
public class RecipeAdapter {

    /**
     * Adapts a single external Spoonacular recipe to the internal format.
     *
     * @param external the raw API response object
     * @return a clean {@link RecipeDTO} for internal use
     */
    public RecipeDTO adapt(SpoonacularRecipeDTO external) {
        RecipeDTO dto = new RecipeDTO();

        dto.setRecipeId(external.getId());
        dto.setTitle(external.getTitle());
        dto.setImageUrl(external.getImage());
        dto.setMatchedIngredientCount(external.getUsedIngredientCount());
        dto.setMissingIngredientCount(external.getMissedIngredientCount());
        dto.setLikes(external.getLikes());

        // Map ingredient objects → simple name strings
        dto.setMatchedIngredients(extractNames(external.getUsedIngredients()));
        dto.setMissingIngredients(extractNames(external.getMissedIngredients()));

        return dto;
    }

    /**
     * Adapts a list of external recipes to internal format.
     *
     * @param externals list of raw API response objects
     * @return list of clean {@link RecipeDTO} objects
     */
    public List<RecipeDTO> adaptList(List<SpoonacularRecipeDTO> externals) {
        if (externals == null) {
            return Collections.emptyList();
        }
        return externals.stream()
                .map(this::adapt)
                .collect(Collectors.toList());
    }

    // ── Private helper ────────────────────────────────────────────────

    /**
     * Extracts ingredient names from Spoonacular's detailed ingredient objects.
     */
    private List<String> extractNames(List<SpoonacularIngredientDTO> ingredients) {
        if (ingredients == null) {
            return Collections.emptyList();
        }
        return ingredients.stream()
                .map(SpoonacularIngredientDTO::getName)
                .collect(Collectors.toList());
    }
}