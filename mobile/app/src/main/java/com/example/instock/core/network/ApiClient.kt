package com.example.instock.core.network

import android.content.Context
import com.example.instock.features.auth.AuthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // For Android emulator -> host machine localhost.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor()

    // Built lazily after init(context) is called so the 401 interceptor
    // receives the application context at construction time.
    private lateinit var okHttpClient: OkHttpClient

    fun init(context: Context) {
        val appContext = context.applicationContext
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(ExpiredTokenInterceptor(appContext))
            .build()
    }

    private fun retrofit(): Retrofit {
        check(::okHttpClient.isInitialized) {
            "ApiClient.init(context) must be called before using the API."
        }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy { retrofit().create(AuthApiService::class.java) }

    val pantryApi: com.example.instock.features.pantry.PantryApiService by lazy {
        retrofit().create(com.example.instock.features.pantry.PantryApiService::class.java)
    }

    val recipeApi: com.example.instock.features.recipes.RecipeApiService by lazy {
        retrofit().create(com.example.instock.features.recipes.RecipeApiService::class.java)
    }

    val favoriteApi: com.example.instock.features.recipes.FavoriteApiService by lazy {
        retrofit().create(com.example.instock.features.recipes.FavoriteApiService::class.java)
    }

    val adminApi: com.example.instock.features.admin.AdminApiService by lazy {
        retrofit().create(com.example.instock.features.admin.AdminApiService::class.java)
    }
}
