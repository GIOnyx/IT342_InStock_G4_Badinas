package edu.cit.badinas.instock.service.recipe;

import edu.cit.badinas.instock.dto.recipe.RecipeDTO;
import edu.cit.badinas.instock.dto.recipe.SpoonacularRecipeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Design Pattern: Facade (Structural) — for the Spoonacular subsystem.
 *
 * <p>Encapsulates all HTTP communication with the Spoonacular API,
 * including URL construction, API-key injection, error handling, and
 * response deserialization.  Consumers (such as {@code RecipeController})
 * call a single method and receive clean {@link RecipeDTO} objects —
 * they never deal with raw HTTP, query parameters, or external JSON.</p>
 *
 * <p>Internally uses the <b>Adapter Pattern</b> ({@link RecipeAdapter})
 * to convert the external data format into the internal model.</p>
 */
@Service
public class RecipeApiFacade {

    private final RecipeAdapter recipeAdapter;
    private final RestClient restClient;
    private final String apiKey;

    private static final String BASE_URL = "https://api.spoonacular.com";

    public RecipeApiFacade(RecipeAdapter recipeAdapter,
                           @Value("${spoonacular.api.key}") String apiKey) {
        this.recipeAdapter = recipeAdapter;
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    /**
     * Searches for recipes that can be made with the given ingredients.
     *
     * @param ingredients comma-separated ingredient names (e.g. "chicken,rice,tomato")
     * @param number      maximum number of results to return (default 10)
     * @return a list of {@link RecipeDTO} in the application's internal format
     */
    public List<RecipeDTO> searchByIngredients(String ingredients, int number) {
        // ── Call the Spoonacular API ──────────────────────────────────
        List<SpoonacularRecipeDTO> externalRecipes = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/findByIngredients")
                        .queryParam("ingredients", ingredients)
                        .queryParam("number", number)
                        .queryParam("ranking", 1)           // maximise used ingredients
                        .queryParam("ignorePantry", true)   // ignore common pantry items
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        // ── Adapter Pattern — convert external format → internal ─────
        return recipeAdapter.adaptList(externalRecipes);
    }
}
