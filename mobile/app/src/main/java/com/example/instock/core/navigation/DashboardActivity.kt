package com.example.instock.core.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.instock.R
import com.example.instock.features.auth.LoginActivity

class DashboardActivity : AppCompatActivity() {



    private lateinit var iconSettings: ImageView
    private lateinit var iconRecipes: ImageView
    private lateinit var iconPantry: ImageView
    private lateinit var iconFavorites: ImageView
    private lateinit var iconProfile: ImageView

    private lateinit var bubbleSettings: FrameLayout
    private lateinit var bubbleRecipes: FrameLayout
    private lateinit var bubblePantry: FrameLayout
    private lateinit var bubbleFavorites: FrameLayout
    private lateinit var bubbleProfile: FrameLayout

    private lateinit var labelSettings: TextView
    private lateinit var labelRecipes: TextView
    private lateinit var labelPantry: TextView
    private lateinit var labelFavorites: TextView
    private lateinit var labelProfile: TextView

    private lateinit var email: String
    private var currentTab: Tab? = null

    private enum class Tab {
        SETTINGS,
        RECIPES,
        PANTRY,
        FAVORITES,
        PROFILE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()

        iconSettings = findViewById(R.id.iconSettings)
        iconRecipes = findViewById(R.id.iconRecipes)
        iconPantry = findViewById(R.id.iconPantry)
        iconFavorites = findViewById(R.id.iconFavorites)
        iconProfile = findViewById(R.id.iconProfile)

        bubbleSettings = findViewById(R.id.bubbleSettings)
        bubbleRecipes = findViewById(R.id.bubbleRecipes)
        bubblePantry = findViewById(R.id.bubblePantry)
        bubbleFavorites = findViewById(R.id.bubbleFavorites)
        bubbleProfile = findViewById(R.id.bubbleProfile)

        labelSettings = findViewById(R.id.labelSettings)
        labelRecipes = findViewById(R.id.labelRecipes)
        labelPantry = findViewById(R.id.labelPantry)
        labelFavorites = findViewById(R.id.labelFavorites)
        labelProfile = findViewById(R.id.labelProfile)

        findViewById<ImageView>(R.id.logoutAction).setOnClickListener {
            logoutAndTerminateSession()
        }

        findViewById<View>(R.id.navSettings).setOnClickListener { switchTab(Tab.SETTINGS) }
        findViewById<View>(R.id.navRecipes).setOnClickListener { switchTab(Tab.RECIPES) }
        findViewById<View>(R.id.navPantry).setOnClickListener { switchTab(Tab.PANTRY) }
        findViewById<View>(R.id.navFavorites).setOnClickListener { switchTab(Tab.FAVORITES) }
        findViewById<View>(R.id.navProfile).setOnClickListener { switchTab(Tab.PROFILE) }

        if (savedInstanceState == null) {
            switchTab(Tab.PANTRY)
        }
    }

    private fun switchTab(tab: Tab) {
        if (currentTab == tab) return

        val fragment = when (tab) {
            Tab.PANTRY -> com.example.instock.features.pantry.PantryFragment.newInstance()
            Tab.RECIPES -> com.example.instock.features.recipes.RecipesFragment.newInstance()
            Tab.FAVORITES -> com.example.instock.features.recipes.FavoritesFragment.newInstance()
            Tab.SETTINGS -> com.example.instock.features.profile.SettingsFragment.newInstance()
            Tab.PROFILE -> com.example.instock.features.profile.ProfileFragment.newInstance()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        applyFooterState(tab)
        currentTab = tab
    }

    private fun applyFooterState(tab: Tab) {
        val muted = ContextCompat.getColor(this, R.color.brand_text_muted)
        val active = ContextCompat.getColor(this, R.color.brand_primary)

        resetFooterState(muted)

        when (tab) {
            Tab.SETTINGS -> setActive(bubbleSettings, iconSettings, labelSettings, active)
            Tab.RECIPES -> setActive(bubbleRecipes, iconRecipes, labelRecipes, active)
            Tab.PANTRY -> setActive(bubblePantry, iconPantry, labelPantry, active)
            Tab.FAVORITES -> setActive(bubbleFavorites, iconFavorites, labelFavorites, active)
            Tab.PROFILE -> setActive(bubbleProfile, iconProfile, labelProfile, active)
        }
    }

    private fun resetFooterState(muted: Int) {
        bubbleSettings.background = null
        bubbleRecipes.background = null
        bubblePantry.background = null
        bubbleFavorites.background = null
        bubbleProfile.background = null

        iconSettings.setColorFilter(muted)
        iconRecipes.setColorFilter(muted)
        iconPantry.setColorFilter(muted)
        iconFavorites.setColorFilter(muted)
        iconProfile.setColorFilter(muted)

        labelSettings.setTextColor(muted)
        labelRecipes.setTextColor(muted)
        labelPantry.setTextColor(muted)
        labelFavorites.setTextColor(muted)
        labelProfile.setTextColor(muted)

        labelSettings.setTypeface(null, android.graphics.Typeface.NORMAL)
        labelRecipes.setTypeface(null, android.graphics.Typeface.NORMAL)
        labelPantry.setTypeface(null, android.graphics.Typeface.NORMAL)
        labelFavorites.setTypeface(null, android.graphics.Typeface.NORMAL)
        labelProfile.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    private fun setActive(bubble: FrameLayout, icon: ImageView, label: TextView, active: Int) {
        bubble.setBackgroundResource(R.drawable.bg_footer_active_circle)
        icon.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
        label.setTextColor(active)
        label.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun logoutAndTerminateSession() {
        getSharedPreferences("instock_session", MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply()
        com.example.instock.core.network.AllergenPrefs.clear()
        com.example.instock.core.network.OfflineCache.clear()

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}
