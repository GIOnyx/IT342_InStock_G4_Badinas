package com.example.instock.network

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
