package com.example.instock.features.admin

import com.google.gson.annotations.SerializedName

data class AdminStatsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: AdminStats
)

data class AdminStats(
    @SerializedName("totalUsers") val totalUsers: Long,
    @SerializedName("totalPantryItems") val totalPantryItems: Long,
    @SerializedName("totalFavorites") val totalFavorites: Long
)
