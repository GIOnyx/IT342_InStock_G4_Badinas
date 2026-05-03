package com.example.instock.core.navigation

import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.instock.R
import com.example.instock.features.favorites.FavoritesActivity
import com.example.instock.features.pantry.PantryActivity
import com.example.instock.features.profile.ProfileActivity
import com.example.instock.features.profile.SettingsActivity
import com.example.instock.features.recipes.RecipesActivity

enum class FooterPage {
    DASHBOARD,
    PANTRY,
    SETTINGS,
    RECIPES,
    PROFILE,
    FAVORITES
}

object FooterNavigation {

    fun setup(activity: AppCompatActivity, currentPage: FooterPage) {
        val muted = ContextCompat.getColor(activity, R.color.brand_text_muted)
        val active = ContextCompat.getColor(activity, R.color.brand_primary)

        val navSettings = activity.findViewById<android.view.View>(R.id.navSettings)
        val navRecipes = activity.findViewById<android.view.View>(R.id.navRecipes)
        val navPantry = activity.findViewById<android.view.View>(R.id.navPantry)
        val navFavorites = activity.findViewById<android.view.View>(R.id.navFavorites)
        val navProfile = activity.findViewById<android.view.View>(R.id.navProfile)

        applyState(activity, muted, active, currentPage)

        navSettings.setOnClickListener { go(activity, currentPage, FooterPage.SETTINGS, SettingsActivity::class.java) }
        navRecipes.setOnClickListener { go(activity, currentPage, FooterPage.RECIPES, RecipesActivity::class.java) }
        navPantry.setOnClickListener { go(activity, currentPage, FooterPage.PANTRY, PantryActivity::class.java) }
        navFavorites.setOnClickListener { go(activity, currentPage, FooterPage.FAVORITES, FavoritesActivity::class.java) }
        navProfile.setOnClickListener { go(activity, currentPage, FooterPage.PROFILE, ProfileActivity::class.java) }
    }

    private fun go(
        activity: AppCompatActivity,
        currentPage: FooterPage,
        targetPage: FooterPage,
        clazz: Class<out AppCompatActivity>
    ) {
        if (currentPage == targetPage) return
        activity.startActivity(Intent(activity, clazz))
    }

    private fun applyState(activity: AppCompatActivity, muted: Int, active: Int, currentPage: FooterPage) {
        val iconSettings = activity.findViewById<ImageView>(R.id.iconSettings)
        val iconRecipes = activity.findViewById<ImageView>(R.id.iconRecipes)
        val iconPantry = activity.findViewById<ImageView>(R.id.iconPantry)
        val iconFavorites = activity.findViewById<ImageView>(R.id.iconFavorites)
        val iconProfile = activity.findViewById<ImageView>(R.id.iconProfile)

        val labelSettings = activity.findViewById<TextView>(R.id.labelSettings)
        val labelRecipes = activity.findViewById<TextView>(R.id.labelRecipes)
        val labelPantry = activity.findViewById<TextView>(R.id.labelPantry)
        val labelFavorites = activity.findViewById<TextView>(R.id.labelFavorites)
        val labelProfile = activity.findViewById<TextView>(R.id.labelProfile)

        iconSettings.setColorFilter(muted)
        iconRecipes.setColorFilter(muted)
        iconFavorites.setColorFilter(muted)
        iconProfile.setColorFilter(muted)

        labelSettings.setTextColor(muted)
        labelRecipes.setTextColor(muted)
        labelPantry.setTextColor(active)
        labelFavorites.setTextColor(muted)
        labelProfile.setTextColor(muted)

        when (currentPage) {
            FooterPage.SETTINGS -> {
                iconSettings.setColorFilter(active)
                labelSettings.setTextColor(active)
            }
            FooterPage.RECIPES -> {
                iconRecipes.setColorFilter(active)
                labelRecipes.setTextColor(active)
            }
            FooterPage.PANTRY, FooterPage.DASHBOARD -> {
                iconPantry.setColorFilter(ContextCompat.getColor(activity, android.R.color.white))
                labelPantry.setTextColor(active)
            }
            FooterPage.FAVORITES -> {
                iconFavorites.setColorFilter(active)
                labelFavorites.setTextColor(active)
            }
            FooterPage.PROFILE -> {
                iconProfile.setColorFilter(active)
                labelProfile.setTextColor(active)
            }
        }
    }
}
