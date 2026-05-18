package com.example.instock.core.network

import android.content.Context
import android.content.Intent
import com.example.instock.features.auth.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class ExpiredTokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        if (response.code == 401) {
            TokenManager.clearSession()
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
        
        return response
    }
}
