package com.example.instock.features.pantry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instock.R

class PantryAdapter(
    private var items: List<PantryItem>,
    private val onDeleteClicked: (PantryItem) -> Unit
) : RecyclerView.Adapter<PantryAdapter.PantryViewHolder>() {

    class PantryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pantry, parent, false)
        return PantryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PantryViewHolder, position: Int) {
        val item = items[position]
        holder.tvItemName.text = item.name

        holder.btnDelete.setOnClickListener {
            onDeleteClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<PantryItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
