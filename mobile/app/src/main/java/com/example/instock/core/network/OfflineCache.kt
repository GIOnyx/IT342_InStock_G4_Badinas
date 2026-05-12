package com.example.instock.core.network

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.instock.features.pantry.PantryItem
import com.example.instock.features.recipes.FavoriteRecipe

/**
 * Simple SharedPreferences-backed offline cache for Pantry and Favorites.
 *
 * Stores JSON-serialised lists so the user can view their data when the
 * device has no network connection (SDD 7.0 — Offline caching).
 */
object OfflineCache {
    private const val PREFS_NAME = "instock_cache"
    private const val KEY_PANTRY = "pantry_items"
    private const val KEY_FAVORITES = "favorite_items"

    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ── Pantry ────────────────────────────────────────────────────────

    fun savePantry(items: List<PantryItem>) {
        prefs?.edit()?.putString(KEY_PANTRY, gson.toJson(items))?.apply()
    }

    fun loadPantry(): List<PantryItem> {
        val json = prefs?.getString(KEY_PANTRY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<PantryItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Favorites ─────────────────────────────────────────────────────

    fun saveFavorites(items: List<FavoriteRecipe>) {
        prefs?.edit()?.putString(KEY_FAVORITES, gson.toJson(items))?.apply()
    }

    fun loadFavorites(): List<FavoriteRecipe> {
        val json = prefs?.getString(KEY_FAVORITES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<FavoriteRecipe>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Clears all cached data (called on logout). */
    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
