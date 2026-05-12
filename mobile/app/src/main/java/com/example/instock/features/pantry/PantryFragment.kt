package com.example.instock.features.pantry

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

class PantryFragment : Fragment() {

    private lateinit var rvPantry: RecyclerView
    private lateinit var adapter: PantryAdapter
    private lateinit var fabAddPantry: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pantry, container, false)
        rvPantry = view.findViewById(R.id.rvPantry)
        fabAddPantry = view.findViewById(R.id.fabAddPantry)

        rvPantry.layoutManager = LinearLayoutManager(requireContext())
        adapter = PantryAdapter(emptyList()) { item ->
            deleteItem(item)
        }
        rvPantry.adapter = adapter

        fabAddPantry.setOnClickListener {
            showAddItemDialog()
        }

        fetchItems()

        return view
    }

    private fun fetchItems() {
        ApiClient.pantryApi.getPantryItems().enqueue(object : Callback<PantryListResponse> {
            override fun onResponse(call: Call<PantryListResponse>, response: Response<PantryListResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    val data = response.body()?.data ?: emptyList()
                    adapter.updateItems(data)
                    // Persist to offline cache
                    OfflineCache.savePantry(data)
                } else {
                    Toast.makeText(requireContext(), "Failed to load pantry", Toast.LENGTH_SHORT).show()
                    loadFromCache()
                }
            }

            override fun onFailure(call: Call<PantryListResponse>, t: Throwable) {
                if (isAdded) {
                    // Network unavailable — serve from cache
                    loadFromCache()
                }
            }
        })
    }

    /** Loads pantry from the local cache and shows an offline notice. */
    private fun loadFromCache() {
        val cached = OfflineCache.loadPantry()
        if (cached.isNotEmpty()) {
            adapter.updateItems(cached)
            Toast.makeText(requireContext(), "Offline — showing cached pantry", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Network error and no cached data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteItem(item: PantryItem) {
        ApiClient.pantryApi.deletePantryItem(item.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    fetchItems()
                    Toast.makeText(requireContext(), "Item removed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to remove item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showAddItemDialog() {
        val input = EditText(requireContext())
        input.hint = "e.g. Tomato"

        AlertDialog.Builder(requireContext())
            .setTitle("Add Pantry Item")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    addItem(name)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addItem(name: String) {
        ApiClient.pantryApi.addPantryItem(PantryAddRequest(name)).enqueue(object : Callback<PantrySingleResponse> {
            override fun onResponse(call: Call<PantrySingleResponse>, response: Response<PantrySingleResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    fetchItems()
                    Toast.makeText(requireContext(), "Item added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PantrySingleResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        fun newInstance() = PantryFragment()
    }
}
