package edu.cit.badinas.instock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRecipeRequest {

    @NotNull(message = "externalRecipeId is required")
    private Long externalRecipeId;

    @NotBlank(message = "title is required")
    private String title;

    private String imageUrl;
    private String summary;
    private Integer likes;
}
