package com.example.instock.features.recipes

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {

    @GET("api/v1/recipes/search")
    fun searchByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10,
        @Query("intolerances") intolerances: String? = null
    ): Call<RecipeListResponse>

    @GET("api/v1/recipes/search-by-name")
    fun searchByRecipeName(
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("intolerances") intolerances: String? = null
    ): Call<RecipeListResponse>
}

interface FavoriteApiService {

    @GET("api/v1/favorites")
    fun getFavorites(): Call<FavoriteListResponse>

    @POST("api/v1/favorites")
    fun addFavorite(@Body request: FavoriteRecipeRequest): Call<FavoriteSingleResponse>

    @DELETE("api/v1/favorites/{id}")
    fun deleteFavorite(@Path("id") id: Long): Call<Void>
}
