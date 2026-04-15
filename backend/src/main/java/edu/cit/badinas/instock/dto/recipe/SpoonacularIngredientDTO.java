package edu.cit.badinas.instock.dto.recipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps a single ingredient object inside the Spoonacular API response
 * (used in {@code usedIngredients}, {@code missedIngredients}, and
 * {@code unusedIngredients} arrays).
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularIngredientDTO {

    private Long id;
    private String name;
    private double amount;
    private String unit;
    private String unitLong;
    private String original;
    private String aisle;
    private String image;
}
