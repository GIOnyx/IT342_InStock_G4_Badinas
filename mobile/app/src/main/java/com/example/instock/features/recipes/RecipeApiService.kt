package com.example.instock.features.recipes

import com.example.instock.core.network.ApiResponse
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

    @GET("api/v1/recipes/search")
    suspend fun searchByIngredientsSuspend(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10,
        @Query("intolerances") intolerances: String? = null
    ): ApiResponse<List<RecipeDTO>>

    @GET("api/v1/recipes/search-by-name")
    fun searchByRecipeName(
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("intolerances") intolerances: String? = null
    ): Call<RecipeListResponse>

    @GET("api/v1/recipes/search-by-name")
    suspend fun searchByRecipeNameSuspend(
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("intolerances") intolerances: String? = null
    ): ApiResponse<List<RecipeDTO>>

    @GET("api/v1/recipes/{id}")
    suspend fun getRecipeDetailSuspend(@Path("id") id: Long): ApiResponse<RecipeDetailDTO>
}

interface FavoriteApiService {

    @GET("api/v1/favorites")
    fun getFavorites(): Call<FavoriteListResponse>

    @GET("api/v1/favorites")
    suspend fun getFavoritesSuspend(): ApiResponse<List<FavoriteRecipe>>

    @POST("api/v1/favorites")
    fun addFavorite(@Body request: FavoriteRecipeRequest): Call<FavoriteSingleResponse>

    @POST("api/v1/favorites")
    suspend fun addFavoriteSuspend(@Body request: FavoriteRecipeRequest): ApiResponse<FavoriteRecipe>

    @DELETE("api/v1/favorites/{id}")
    fun deleteFavorite(@Path("id") id: Long): Call<Void>

    @DELETE("api/v1/favorites/{id}")
    suspend fun deleteFavoriteSuspend(@Path("id") id: Long): ApiResponse<Unit>
}
