package com.example.instock.features.auth

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/register")
    fun register(@Body request: RegisterRequest): Call<JsonObject>

    @POST("api/v1/auth/login")
    fun login(@Body request: LoginRequest): Call<JsonObject>
}
