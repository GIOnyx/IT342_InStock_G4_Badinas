package edu.cit.badinas.instock.recipes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularInstructionGroupDTO {
    private List<SpoonacularInstructionStepDTO> steps;
}