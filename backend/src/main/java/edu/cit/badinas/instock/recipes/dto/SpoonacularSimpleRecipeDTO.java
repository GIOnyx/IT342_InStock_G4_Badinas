package edu.cit.badinas.instock.recipes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularSimpleRecipeDTO {
    private Long id;
    private String title;
    private String image;
}