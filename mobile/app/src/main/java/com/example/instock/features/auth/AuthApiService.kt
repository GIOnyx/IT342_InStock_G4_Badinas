package com.example.instock.features.auth

import com.example.instock.core.network.ApiResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {
    @POST("api/v1/auth/register")
    fun register(@Body request: RegisterRequest): Call<JsonObject>

    @POST("api/v1/auth/register")
    suspend fun registerSuspend(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("api/v1/auth/login")
    fun login(@Body request: LoginRequest): Call<JsonObject>

    @POST("api/v1/auth/login")
    suspend fun loginSuspend(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @GET("api/v1/auth/me")
    fun getMe(): Call<AuthMeResponse>

    @GET("api/v1/auth/me")
    suspend fun getMeSuspend(): ApiResponse<AuthResponse>

    @PUT("api/v1/auth/me")
    fun updateMe(@Body request: UpdateProfileRequest): Call<AuthMeResponse>

    @PUT("api/v1/auth/me")
    suspend fun updateMeSuspend(@Body request: UpdateProfileRequest): ApiResponse<AuthResponse>

    @PUT("api/v1/auth/me/password")
    fun changePassword(@Body request: ChangePasswordRequest): Call<AuthMeResponse>

    @PUT("api/v1/auth/me/password")
    suspend fun changePasswordSuspend(@Body request: ChangePasswordRequest): ApiResponse<AuthResponse>
}
