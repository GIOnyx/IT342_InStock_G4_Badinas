package com.example.instock.core.network

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton that persists the user's selected allergens (intolerances) in
 * SharedPreferences so they survive app restarts and logouts.
 *
 * Values are stored as a comma-separated string matching Spoonacular's
 * `intolerances` parameter (e.g. "gluten,dairy,peanut").
 */
object AllergenPrefs {
    private const val PREFS_NAME = "instock_prefs"
    private const val KEY_ALLERGENS = "allergens"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** Returns the saved allergen set (may be empty). */
    fun getAllergens(): Set<String> {
        val raw = prefs?.getString(KEY_ALLERGENS, "") ?: ""
        return if (raw.isBlank()) emptySet()
        else raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    /** Persists the allergen set. */
    fun saveAllergens(allergens: Set<String>) {
        prefs?.edit()?.putString(KEY_ALLERGENS, allergens.joinToString(","))?.apply()
    }

    /**
     * Returns a comma-separated string ready to pass as the Spoonacular
     * `intolerances` query param, or null if no allergens are selected.
     */
    fun getIntolerancesParam(): String? {
        val set = getAllergens()
        return if (set.isEmpty()) null else set.joinToString(",")
    }

    /** Clears all saved allergens (called on logout). */
    fun clear() {
        prefs?.edit()?.remove(KEY_ALLERGENS)?.apply()
    }
}
