package edu.cit.badinas.instock.dto.recipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularComplexSearchResponseDTO {
    private List<SpoonacularSimpleRecipeDTO> results;
}
