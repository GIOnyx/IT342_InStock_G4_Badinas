package com.example.instock.features.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instock.R
import com.example.instock.core.network.AllergenPrefs
import com.example.instock.core.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipesFragment : Fragment() {

    private lateinit var rvRecipes: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var etSearchQuery: EditText
    private lateinit var btnSearch: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipes, container, false)
        rvRecipes = view.findViewById(R.id.rvRecipes)
        etSearchQuery = view.findViewById(R.id.etSearchQuery)
        btnSearch = view.findViewById(R.id.btnSearch)

        rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter(emptyList()) { item ->
            saveFavorite(item)
        }
        rvRecipes.adapter = adapter

        btnSearch.setOnClickListener {
            val query = etSearchQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                searchRecipes(query)
            }
        }

        return view
    }

    private fun searchRecipes(query: String) {
        val intolerances = AllergenPrefs.getIntolerancesParam()

        ApiClient.recipeApi.searchByRecipeName(
            query = query,
            intolerances = intolerances
        ).enqueue(object : Callback<RecipeListResponse> {
            override fun onResponse(call: Call<RecipeListResponse>, response: Response<RecipeListResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    val data = response.body()?.data ?: emptyList()
                    adapter.updateItems(data)
                    // Show allergen filter toast (AC-4)
                    if (intolerances != null) {
                        Toast.makeText(
                            requireContext(),
                            "Recipes filtered — excluding: $intolerances",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to search recipes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipeListResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun saveFavorite(item: RecipeDTO) {
        val request = FavoriteRecipeRequest(
            externalRecipeId = item.recipeId,
            title = item.title,
            imageUrl = item.imageUrl,
            summary = "Matched args: ${item.matchedIngredientCount}",
            likes = item.likes
        )

        ApiClient.favoriteApi.addFavorite(request).enqueue(object : Callback<FavoriteSingleResponse> {
            override fun onResponse(call: Call<FavoriteSingleResponse>, response: Response<FavoriteSingleResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Saved to favorites!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to save or already saved", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavoriteSingleResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        fun newInstance() = RecipesFragment()
    }
}
