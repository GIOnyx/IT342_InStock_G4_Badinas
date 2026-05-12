package com.example.instock.features.recipes

data class RecipeDTO(
    val recipeId: Long,
    val title: String,
    val imageUrl: String?,
    val matchedIngredientCount: Int,
    val missingIngredientCount: Int,
    val matchedIngredients: List<String>?,
    val missingIngredients: List<String>?,
    val likes: Int
)

data class RecipeListResponse(
    val success: Boolean,
    val message: String,
    val data: List<RecipeDTO>
)

data class FavoriteRecipe(
    val id: Long,
    val externalRecipeId: Long,
    val title: String,
    val imageUrl: String,
    val summary: String,
    val likes: Int,
    val savedAt: String?
)

data class FavoriteRecipeRequest(
    val externalRecipeId: Long,
    val title: String,
    val imageUrl: String?,
    val summary: String?,
    val likes: Int
)

data class FavoriteListResponse(
    val success: Boolean,
    val message: String,
    val data: List<FavoriteRecipe>
)

data class FavoriteSingleResponse(
    val success: Boolean,
    val message: String,
    val data: FavoriteRecipe?
)
