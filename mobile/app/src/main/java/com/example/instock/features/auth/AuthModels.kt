package com.example.instock.features.auth

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String,
    val authType: String = "password"
)

data class AuthResponse(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: String,
    val avatarUrl: String?,
    val token: String?,
    val dietaryPreferences: List<String>?
)

data class AuthMeResponse(
    val success: Boolean,
    val message: String,
    val data: AuthResponse
)

data class UpdateProfileRequest(
    val fullName: String,
    val dietaryPreferences: List<String>?
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
