package com.example.instock.features.pantry

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PantryApiService {

    @GET("api/v1/stock")
    fun getPantryItems(): Call<PantryListResponse>

    @POST("api/v1/stock")
    fun addPantryItem(@Body request: PantryAddRequest): Call<PantrySingleResponse>

    @DELETE("api/v1/stock/{id}")
    fun deletePantryItem(@Path("id") id: Long): Call<Void>
}
