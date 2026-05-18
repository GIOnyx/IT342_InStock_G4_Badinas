package com.example.instock.features.recipes

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.instock.R
import com.example.instock.core.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
// Wait, do I have Glide or Picasso for images? Usually apps use one. If it's not present, I won't crash, just won't load the URL. I'll omit image loading logic for now or use standard ImageView.

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var tvRecipeTitle: TextView
    private lateinit var tvRecipeReadyIn: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var detailContainer: View
    private lateinit var tvUsedIngredients: TextView
    private lateinit var tvMissingIngredients: TextView
    private lateinit var tvInstructions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        tvRecipeTitle = findViewById(R.id.tvRecipeTitle)
        tvRecipeReadyIn = findViewById(R.id.tvRecipeReadyIn)
        progressBar = findViewById(R.id.progressBar)
        detailContainer = findViewById(R.id.detailContainer)
        tvUsedIngredients = findViewById(R.id.tvUsedIngredients)
        tvMissingIngredients = findViewById(R.id.tvMissingIngredients)
        tvInstructions = findViewById(R.id.tvInstructions)

        val recipeId = intent.getLongExtra("RECIPE_ID", -1L)
        if (recipeId == -1L) {
            Toast.makeText(this, "Invalid Recipe ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchRecipeDetail(recipeId)
    }

    private fun fetchRecipeDetail(id: Long) {
        progressBar.visibility = View.VISIBLE
        detailContainer.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = ApiClient.recipeApi.getRecipeDetailSuspend(id)
                progressBar.visibility = View.GONE

                if (response.success && response.data != null) {
                    val detail = response.data
                    detailContainer.visibility = View.VISIBLE

                    tvRecipeTitle.text = detail.title ?: "Unknown Recipe"
                    tvRecipeReadyIn.text = "Ready in ${detail.readyInMinutes ?: "?"} minutes (Serves ${detail.servings ?: "?"})"
                    
                    val usedStr = detail.usedIngredients?.joinToString("\n") { "- " + it.name } ?: "None"
                    val missingStr = detail.missedIngredients?.joinToString("\n") { "- " + it.name } ?: "None"
                    
                    tvUsedIngredients.text = usedStr
                    tvMissingIngredients.text = missingStr
                    tvInstructions.text = detail.instructions ?: "No instructions provided."

                } else {
                    Toast.makeText(this@RecipeDetailActivity, response.message ?: "Failed to load detail", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: HttpException) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@RecipeDetailActivity, "Network Error", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@RecipeDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
