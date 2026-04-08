package com.example.instock

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var contentContainer: View
    private lateinit var pageTitle: TextView
    private lateinit var pageSubtitle: TextView
    private lateinit var featureTitle: TextView
    private lateinit var featureBody: TextView

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

        contentContainer = findViewById(R.id.contentContainer)
        pageTitle = findViewById(R.id.pageTitle)
        pageSubtitle = findViewById(R.id.pageSubtitle)
        featureTitle = findViewById(R.id.featureTitle)
        featureBody = findViewById(R.id.featureBody)

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

        findViewById<View>(R.id.navSettings).setOnClickListener { switchTab(Tab.SETTINGS, true) }
        findViewById<View>(R.id.navRecipes).setOnClickListener { switchTab(Tab.RECIPES, true) }
        findViewById<View>(R.id.navPantry).setOnClickListener { switchTab(Tab.PANTRY, true) }
        findViewById<View>(R.id.navFavorites).setOnClickListener { switchTab(Tab.FAVORITES, true) }
        findViewById<View>(R.id.navProfile).setOnClickListener { switchTab(Tab.PROFILE, true) }

        switchTab(Tab.PANTRY, false)
    }

    private fun switchTab(tab: Tab, animated: Boolean) {
        if (currentTab == tab) return

        if (!animated || currentTab == null) {
            updateTabContent(tab)
            applyFooterState(tab)
            currentTab = tab
            return
        }

        contentContainer.animate()
            .alpha(0f)
            .translationY(16f)
            .setDuration(110)
            .withEndAction {
                updateTabContent(tab)
                applyFooterState(tab)
                currentTab = tab

                contentContainer.translationY = -12f
                contentContainer.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(170)
                    .start()
            }
            .start()
    }

    private fun updateTabContent(tab: Tab) {
        when (tab) {
            Tab.PANTRY -> {
                pageTitle.text = getString(R.string.pantry_title)
                pageSubtitle.text = getString(R.string.dashboard_subtitle, email)
                featureTitle.text = getString(R.string.dashboard_inventory_title)
                featureBody.text = getString(R.string.dashboard_inventory_text)
            }
            Tab.SETTINGS -> {
                pageTitle.text = getString(R.string.settings_title)
                pageSubtitle.text = getString(R.string.settings_subtitle)
                featureTitle.text = getString(R.string.settings_feature_title)
                featureBody.text = getString(R.string.settings_feature_text)
            }
            Tab.RECIPES -> {
                pageTitle.text = getString(R.string.recipes_title)
                pageSubtitle.text = getString(R.string.recipes_subtitle)
                featureTitle.text = getString(R.string.dashboard_recipe_title)
                featureBody.text = getString(R.string.dashboard_recipe_text)
            }
            Tab.FAVORITES -> {
                pageTitle.text = getString(R.string.favorites_title)
                pageSubtitle.text = getString(R.string.favorites_subtitle)
                featureTitle.text = getString(R.string.favorites_feature_title)
                featureBody.text = getString(R.string.favorites_feature_text)
            }
            Tab.PROFILE -> {
                pageTitle.text = getString(R.string.profile_title)
                pageSubtitle.text = getString(R.string.profile_subtitle)
                featureTitle.text = getString(R.string.profile_feature_title)
                featureBody.text = getString(R.string.profile_feature_text)
            }
        }
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

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}
