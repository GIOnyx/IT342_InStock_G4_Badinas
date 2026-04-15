package edu.cit.badinas.instock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PantryItemRequest {

    @NotBlank(message = "Ingredient name is required")
    private String name;
}
