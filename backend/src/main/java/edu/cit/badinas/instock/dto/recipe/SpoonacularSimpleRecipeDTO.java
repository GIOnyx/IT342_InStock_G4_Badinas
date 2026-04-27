package edu.cit.badinas.instock.dto.recipe;

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
