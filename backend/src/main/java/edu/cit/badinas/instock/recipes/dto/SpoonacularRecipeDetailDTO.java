package edu.cit.badinas.instock.recipes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularRecipeDetailDTO {
    private Long id;
    private String title;
    private String image;
    private String summary;
    private Integer readyInMinutes;
    private Integer servings;
    private String sourceUrl;
    private String instructions;
    private List<SpoonacularInstructionGroupDTO> analyzedInstructions;
    private List<SpoonacularExtendedIngredientDTO> extendedIngredients;
}