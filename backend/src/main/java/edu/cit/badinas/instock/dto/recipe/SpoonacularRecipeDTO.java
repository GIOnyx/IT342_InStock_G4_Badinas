package edu.cit.badinas.instock.dto.recipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Maps the raw JSON returned by the Spoonacular "Search Recipes by Ingredients"
 * endpoint.  Field names match the external API exactly.
 *
 * <p>This class is the "Adaptee" in the Adapter Pattern — it represents the
 * incompatible external interface that must be translated into the
 * application's internal {@link RecipeDTO} format.</p>
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularRecipeDTO {

    private Long id;
    private String title;
    private String image;
    private String imageType;
    private int usedIngredientCount;
    private int missedIngredientCount;
    private int likes;

    private List<SpoonacularIngredientDTO> usedIngredients;
    private List<SpoonacularIngredientDTO> missedIngredients;
    private List<SpoonacularIngredientDTO> unusedIngredients;
}
