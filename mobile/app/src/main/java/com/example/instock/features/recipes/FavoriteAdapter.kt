package com.example.instock.features.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instock.R

class FavoriteAdapter(
    private var items: List<FavoriteRecipe>,
    private val onDeleteClicked: (FavoriteRecipe) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecipeTitle: TextView = view.findViewById(R.id.tvRecipeTitle)
        val tvMatchedIngredients: TextView = view.findViewById(R.id.tvMatchedIngredients)
        val tvLikes: TextView = view.findViewById(R.id.tvLikes)
        val btnFavorite: ImageView = view.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = items[position]
        holder.tvRecipeTitle.text = item.title
        holder.tvMatchedIngredients.text = item.summary
        holder.tvLikes.text = "Likes: ${item.likes}"

        // Change favorite icon to a delete icon or color it differently since it's already favorited
        holder.btnFavorite.setImageResource(android.R.drawable.ic_menu_delete)

        holder.btnFavorite.setOnClickListener {
            onDeleteClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<FavoriteRecipe>) {
        items = newItems
        notifyDataSetChanged()
    }
}
