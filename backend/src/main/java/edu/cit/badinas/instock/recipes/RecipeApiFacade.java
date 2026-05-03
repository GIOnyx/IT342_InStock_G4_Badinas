package edu.cit.badinas.instock.recipes;

import edu.cit.badinas.instock.recipes.dto.RecipeDTO;
import edu.cit.badinas.instock.recipes.dto.RecipeDetailDTO;
import edu.cit.badinas.instock.recipes.dto.SpoonacularComplexSearchResponseDTO;
import edu.cit.badinas.instock.recipes.dto.SpoonacularInstructionGroupDTO;
import edu.cit.badinas.instock.recipes.dto.SpoonacularInstructionStepDTO;
import edu.cit.badinas.instock.recipes.dto.SpoonacularRecipeDetailDTO;
import edu.cit.badinas.instock.recipes.dto.SpoonacularRecipeDTO;
import edu.cit.badinas.instock.recipes.dto.SpoonacularSimpleRecipeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    /**
     * Searches recipes by title/name query.
     */
    public List<RecipeDTO> searchByRecipeTitle(String query, int number) {
        SpoonacularComplexSearchResponseDTO external = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/complexSearch")
                        .queryParam("query", query)
                        .queryParam("number", number)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .body(SpoonacularComplexSearchResponseDTO.class);

        if (external == null || external.getResults() == null) {
            return List.of();
        }

        return external.getResults().stream()
                .map(this::mapSimpleRecipe)
                .collect(Collectors.toList());
    }

    private RecipeDTO mapSimpleRecipe(SpoonacularSimpleRecipeDTO source) {
        RecipeDTO dto = new RecipeDTO();
        dto.setRecipeId(source.getId());
        dto.setTitle(source.getTitle());
        dto.setImageUrl(source.getImage());
        dto.setMatchedIngredientCount(0);
        dto.setMissingIngredientCount(0);
        dto.setMatchedIngredients(List.of());
        dto.setMissingIngredients(List.of());
        dto.setLikes(0);
        return dto;
    }

    /**
     * Fetches full recipe information by recipe id.
     */
    public RecipeDetailDTO getRecipeDetails(Long recipeId) {
        SpoonacularRecipeDetailDTO external = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/{id}/information")
                        .queryParam("includeNutrition", false)
                        .queryParam("apiKey", apiKey)
                        .build(recipeId))
                .retrieve()
                .body(SpoonacularRecipeDetailDTO.class);

        if (external == null) {
            throw new RuntimeException("Recipe details not found");
        }

        RecipeDetailDTO dto = new RecipeDetailDTO();
        dto.setRecipeId(external.getId());
        dto.setTitle(external.getTitle());
        dto.setImageUrl(external.getImage());
        dto.setSummary(external.getSummary());
        dto.setReadyInMinutes(external.getReadyInMinutes());
        dto.setServings(external.getServings());
        dto.setSourceUrl(external.getSourceUrl());

        List<String> ingredients = external.getExtendedIngredients() == null
                ? List.of()
                : external.getExtendedIngredients().stream()
                .map(ingredient -> ingredient.getOriginal() == null ? ingredient.getName() : ingredient.getOriginal())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        dto.setIngredients(ingredients);

        List<String> instructions = extractInstructionSteps(external);
        dto.setInstructions(instructions);

        return dto;
    }

    private List<String> extractInstructionSteps(SpoonacularRecipeDetailDTO external) {
        if (external.getAnalyzedInstructions() != null && !external.getAnalyzedInstructions().isEmpty()) {
            return external.getAnalyzedInstructions().stream()
                    .map(SpoonacularInstructionGroupDTO::getSteps)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .map(SpoonacularInstructionStepDTO::getStep)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        if (external.getInstructions() != null && !external.getInstructions().isBlank()) {
            return List.of(external.getInstructions());
        }

        return List.of();
    }
}