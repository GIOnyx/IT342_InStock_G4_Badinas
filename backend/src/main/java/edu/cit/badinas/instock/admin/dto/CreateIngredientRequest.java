package edu.cit.badinas.instock.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateIngredientRequest {
    @NotBlank(message = "Ingredient name is required")
    private String name;

    private String category;
}
