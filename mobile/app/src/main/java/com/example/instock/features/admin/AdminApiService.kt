package com.example.instock.features.admin

import retrofit2.Call
import retrofit2.http.GET

interface AdminApiService {
    @GET("api/v1/admin/stats")
    fun getStats(): Call<AdminStatsResponse>
}
