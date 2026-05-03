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
