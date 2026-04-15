package edu.cit.badinas.instock.dto.recipe;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RecipeDetailDTO {
    private Long recipeId;
    private String title;
    private String imageUrl;
    private String summary;
    private Integer readyInMinutes;
    private Integer servings;
    private String sourceUrl;
    private List<String> ingredients;
    private List<String> instructions;
}
