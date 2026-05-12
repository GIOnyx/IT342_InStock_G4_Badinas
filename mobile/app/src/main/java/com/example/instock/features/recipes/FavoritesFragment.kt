package com.example.instock.features.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instock.R
import com.example.instock.core.network.ApiClient
import com.example.instock.core.network.OfflineCache
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        rvFavorites = view.findViewById(R.id.rvFavorites)

        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        adapter = FavoriteAdapter(emptyList()) { item ->
            removeFavorite(item)
        }
        rvFavorites.adapter = adapter

        fetchFavorites()

        return view
    }

    private fun fetchFavorites() {
        ApiClient.favoriteApi.getFavorites().enqueue(object : Callback<FavoriteListResponse> {
            override fun onResponse(call: Call<FavoriteListResponse>, response: Response<FavoriteListResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    val data = response.body()?.data ?: emptyList()
                    adapter.updateItems(data)
                    // Persist to offline cache
                    OfflineCache.saveFavorites(data)
                } else {
                    Toast.makeText(requireContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show()
                    loadFromCache()
                }
            }

            override fun onFailure(call: Call<FavoriteListResponse>, t: Throwable) {
                if (isAdded) {
                    // Network unavailable — serve from cache
                    loadFromCache()
                }
            }
        })
    }

    /** Loads favorites from the local cache and shows an offline notice. */
    private fun loadFromCache() {
        val cached = OfflineCache.loadFavorites()
        if (cached.isNotEmpty()) {
            adapter.updateItems(cached)
            Toast.makeText(requireContext(), "Offline — showing cached favorites", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Network error and no cached data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFavorite(item: FavoriteRecipe) {
        ApiClient.favoriteApi.deleteFavorite(item.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    fetchFavorites()
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to remove favorite", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}
