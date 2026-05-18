package com.example.instock.features.admin

import retrofit2.Response
import retrofit2.http.GET

interface AdminApiService {

    /** Returns aggregate platform statistics (totalUsers, totalPantryItems, totalFavorites). */
    @GET("/api/v1/admin/stats")
    suspend fun getStats(): Response<AdminStatsResponse>

    /** Returns the full password-safe list of all registered users. */
    @GET("/api/v1/admin/users")
    suspend fun getUsers(): Response<AdminUsersResponse>
}
