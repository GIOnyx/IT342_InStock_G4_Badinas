package com.example.instock.core.network

import android.content.Context
import com.example.instock.R
import com.example.instock.features.auth.AuthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var baseUrl: String? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor()
    private val errorInterceptor = ErrorInterceptor()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .addInterceptor(errorInterceptor)
        .build()

    fun init(context: Context) {
        baseUrl = context.getString(R.string.base_url)
    }

    private fun getRetrofit(): Retrofit {
        val resolvedBaseUrl = baseUrl ?: error("ApiClient not initialized. Call ApiClient.init(context).")
        return retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl(resolvedBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofit = it }
        }
    }

    var authApiForTest: AuthApiService? = null

    val authApi: AuthApiService
        get() = authApiForTest ?: authApiLazy

    private val authApiLazy: AuthApiService by lazy {
        getRetrofit().create(AuthApiService::class.java)
    }

    val pantryApi: com.example.instock.features.pantry.PantryApiService by lazy {
        getRetrofit().create(com.example.instock.features.pantry.PantryApiService::class.java)
    }

    val recipeApi: com.example.instock.features.recipes.RecipeApiService by lazy {
        getRetrofit().create(com.example.instock.features.recipes.RecipeApiService::class.java)
    }

    val favoriteApi: com.example.instock.features.recipes.FavoriteApiService by lazy {
        getRetrofit().create(com.example.instock.features.recipes.FavoriteApiService::class.java)
    }

    val adminApi: com.example.instock.features.admin.AdminApiService by lazy {
        getRetrofit().create(com.example.instock.features.admin.AdminApiService::class.java)
    }
}
