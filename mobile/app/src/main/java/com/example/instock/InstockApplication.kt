package com.example.instock

import android.app.Application
import com.example.instock.core.network.AllergenPrefs
import com.example.instock.core.network.ApiClient
import com.example.instock.core.network.OfflineCache
import com.example.instock.core.network.TokenManager

class InstockApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        ApiClient.init(this)
        AllergenPrefs.init(this)
        OfflineCache.init(this)
    }
}
