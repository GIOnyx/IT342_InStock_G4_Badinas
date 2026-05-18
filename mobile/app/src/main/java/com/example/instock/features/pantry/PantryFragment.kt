package com.example.instock.features.pantry

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
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
    private lateinit var pantryProgressBar: ProgressBar
    private lateinit var pantryEmptyState: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pantry, container, false)
        rvPantry = view.findViewById(R.id.rvPantry)
        fabAddPantry = view.findViewById(R.id.fabAddPantry)
        pantryProgressBar = view.findViewById(R.id.pantryProgressBar)
        pantryEmptyState = view.findViewById(R.id.pantryEmptyState)

        rvPantry.layoutManager = LinearLayoutManager(requireContext())
        adapter = PantryAdapter(emptyList()) { item ->
            // Original click to delete logic removed from adapter, handled by swipe instead
        }
        rvPantry.adapter = adapter

        // Setup Swipe-to-Delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.removeItemAt(position)
                deleteItem(item, position)
                updateEmptyState(adapter.itemCount == 0)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.parseColor("#F44336")) // Material Red
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                val icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)
                icon?.let {
                    val margin = (itemView.height - it.intrinsicHeight) / 2
                    it.setBounds(
                        itemView.right - margin - it.intrinsicWidth,
                        itemView.top + margin,
                        itemView.right - margin,
                        itemView.bottom - margin
                    )
                    it.setTint(Color.WHITE)
                    it.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        itemTouchHelper.attachToRecyclerView(rvPantry)

        fabAddPantry.setOnClickListener {
            showAddItemDialog()
        }

        fetchItems()

        return view
    }

    private fun fetchItems() {
        pantryProgressBar.visibility = View.VISIBLE
        rvPantry.visibility = View.GONE
        pantryEmptyState.visibility = View.GONE

        ApiClient.pantryApi.getPantryItems().enqueue(object : Callback<PantryListResponse> {
            override fun onResponse(call: Call<PantryListResponse>, response: Response<PantryListResponse>) {
                if (!isAdded) return
                pantryProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val data = response.body()?.data ?: emptyList()
                    adapter.updateItems(data)
                    OfflineCache.savePantry(data)
                    updateEmptyState(data.isEmpty())
                } else {
                    Toast.makeText(requireContext(), "Failed to load pantry", Toast.LENGTH_SHORT).show()
                    loadFromCache()
                }
            }

            override fun onFailure(call: Call<PantryListResponse>, t: Throwable) {
                if (isAdded) {
                    pantryProgressBar.visibility = View.GONE
                    loadFromCache()
                }
            }
        })
    }

    private fun loadFromCache() {
        val cached = OfflineCache.loadPantry()
        if (cached.isNotEmpty()) {
            adapter.updateItems(cached)
            Toast.makeText(requireContext(), "Offline — showing cached pantry", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Network error and no cached data", Toast.LENGTH_SHORT).show()
        }
        updateEmptyState(cached.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            pantryEmptyState.visibility = View.VISIBLE
            rvPantry.visibility = View.GONE
        } else {
            pantryEmptyState.visibility = View.GONE
            rvPantry.visibility = View.VISIBLE
        }
    }

    private fun deleteItem(item: PantryItem, position: Int) {
        ApiClient.pantryApi.deletePantryItem(item.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    // Update cache for other features querying it locally
                    val cached = OfflineCache.loadPantry().toMutableList()
                    cached.removeIf { it.id == item.id }
                    OfflineCache.savePantry(cached)
                    Toast.makeText(requireContext(), "Item removed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to remove item", Toast.LENGTH_SHORT).show()
                    fetchItems() // Revert local change
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                    fetchItems() // Revert local change
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
        pantryProgressBar.visibility = View.VISIBLE
        ApiClient.pantryApi.addPantryItem(PantryAddRequest(name)).enqueue(object : Callback<PantrySingleResponse> {
            override fun onResponse(call: Call<PantrySingleResponse>, response: Response<PantrySingleResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    fetchItems()
                    Toast.makeText(requireContext(), "Item added", Toast.LENGTH_SHORT).show()
                } else {
                    pantryProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PantrySingleResponse>, t: Throwable) {
                if (isAdded) {
                    pantryProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        fun newInstance() = PantryFragment()
    }
}
