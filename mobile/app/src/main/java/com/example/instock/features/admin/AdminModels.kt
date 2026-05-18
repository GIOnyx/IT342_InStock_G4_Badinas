package com.example.instock.features.admin

/**
 * Password-safe DTO mirroring the backend's UserSummaryDTO.
 * Does NOT contain passwordHash or any sensitive credential field.
 */
data class UserSummaryDTO(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val role: String?,
    val isVerified: Boolean?
)

/** Wraps the backend's standard list response envelope for users. */
data class AdminUsersResponse(
    val success: Boolean,
    val data: List<UserSummaryDTO>?,
    val message: String?
)

/** Wraps the backend's standard stats response envelope. */
data class AdminStatsResponse(
    val success: Boolean,
    val data: AdminStatsData?,
    val message: String?
)

data class AdminStatsData(
    val totalUsers: Long?,
    val totalPantryItems: Long?,
    val totalFavorites: Long?
)
