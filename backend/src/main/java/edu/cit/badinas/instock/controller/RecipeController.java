package edu.cit.badinas.instock.controller;

import edu.cit.badinas.instock.dto.ApiResponse;
import edu.cit.badinas.instock.dto.recipe.RecipeDTO;
import edu.cit.badinas.instock.dto.recipe.RecipeDetailDTO;
import edu.cit.badinas.instock.service.recipe.RecipeApiFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the Recipe Suggestion subsystem.
 *
 * <p>Follows the same Facade pattern as {@code AuthController} —
 * this controller has a <b>single dependency</b> on
 * {@link RecipeApiFacade} and contains zero business logic.</p>
 */
@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeApiFacade recipeApiFacade;

    /**
     * Search for recipes by ingredients.
     *
     * @param ingredients comma-separated ingredient names (e.g. "chicken,rice,tomato")
     * @param number      max results to return (default 10, max 100)
     * @return list of matched recipes in the internal {@link RecipeDTO} format
     *
     * <p>Example: {@code GET /api/v1/recipes/search?ingredients=chicken,rice&number=5}</p>
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RecipeDTO>>> searchByIngredients(
            @RequestParam String ingredients,
            @RequestParam(defaultValue = "10") int number) {

        List<RecipeDTO> recipes = recipeApiFacade.searchByIngredients(ingredients, number);

        return ResponseEntity.ok(
                ApiResponse.success("Found " + recipes.size() + " recipes", recipes));
    }

    @GetMapping("/search-by-name")
    public ResponseEntity<ApiResponse<List<RecipeDTO>>> searchByRecipeName(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int number) {

        List<RecipeDTO> recipes = recipeApiFacade.searchByRecipeTitle(query, number);
        return ResponseEntity.ok(
                ApiResponse.success("Found " + recipes.size() + " recipes", recipes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecipeDetailDTO>> getRecipeDetails(
            @PathVariable Long id) {

        RecipeDetailDTO details = recipeApiFacade.getRecipeDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Recipe details loaded", details));
    }
}
