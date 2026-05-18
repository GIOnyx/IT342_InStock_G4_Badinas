package com.example.instock.features.pantry

import com.example.instock.core.network.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PantryApiService {

    @GET("api/v1/stock")
    fun getPantryItems(): Call<PantryListResponse>

    @GET("api/v1/stock")
    suspend fun getPantryItemsSuspend(): ApiResponse<List<PantryItem>>

    @POST("api/v1/stock")
    fun addPantryItem(@Body request: PantryAddRequest): Call<PantrySingleResponse>

    @POST("api/v1/stock")
    suspend fun addPantryItemSuspend(@Body request: PantryAddRequest): ApiResponse<PantryItem>

    @DELETE("api/v1/stock/{id}")
    fun deletePantryItem(@Path("id") id: Long): Call<Void>

    @DELETE("api/v1/stock/{id}")
    suspend fun deletePantryItemSuspend(@Path("id") id: Long): ApiResponse<Unit>
}
