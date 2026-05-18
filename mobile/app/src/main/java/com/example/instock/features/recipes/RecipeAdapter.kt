package com.example.instock.features.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instock.R

class RecipeAdapter(
    private var items: List<RecipeDTO>,
    private val onFavoriteClicked: (RecipeDTO) -> Unit,
    private val onItemClicked: (RecipeDTO) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecipeTitle: TextView = view.findViewById(R.id.tvRecipeTitle)
        val tvMatchedIngredients: TextView = view.findViewById(R.id.tvMatchedIngredients)
        val tvLikes: TextView = view.findViewById(R.id.tvLikes)
        val btnFavorite: ImageView = view.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = items[position]
        holder.tvRecipeTitle.text = item.title
        holder.tvMatchedIngredients.text = "Matched: ${item.matchedIngredientCount} / Missing: ${item.missingIngredientCount}"
        holder.tvLikes.text = "Likes: ${item.likes}"

        holder.btnFavorite.setOnClickListener {
            onFavoriteClicked(item)
        }

        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<RecipeDTO>) {
        items = newItems
        notifyDataSetChanged()
    }
}
