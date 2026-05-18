package com.example.instock

import android.app.Activity
import android.app.Application
import android.os.Bundle
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

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }
            override fun onActivityPaused(activity: Activity) {
                if (currentActivity === activity) {
                    currentActivity = null
                }
            }
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    companion object {
        var currentActivity: Activity? = null
            private set
    }
}
