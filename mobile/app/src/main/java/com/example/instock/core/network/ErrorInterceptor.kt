package com.example.instock.core.network

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import com.example.instock.InstockApplication
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: Exception) {
            if (e is SocketTimeoutException || e is UnknownHostException || e is ConnectException) {
                Handler(Looper.getMainLooper()).post {
                    InstockApplication.currentActivity?.let { activity ->
                        if (!activity.isFinishing && !activity.isDestroyed) {
                            AlertDialog.Builder(activity)
                                .setTitle("Connection Error")
                                .setMessage("Unable to reach the server. Please check your internet connection and try again.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
                throw IOException("Network error: ${e.message}", e)
            }
            throw e
        }
    }
}
