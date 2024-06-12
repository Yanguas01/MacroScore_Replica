package es.upm.macroscore.ui.home.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import es.upm.macroscore.R

class SavedMealsAdapter(
    private val onDelete: (mealName: String) -> Unit
) : ListAdapter<String, SavedMealsViewHolder>(SavedMealsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedMealsViewHolder {
        return SavedMealsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_saved_meals, parent, false),
            onDelete
        )
    }

    override fun onBindViewHolder(holder: SavedMealsViewHolder, position: Int) {
        holder.bind(getItem(position), position == itemCount - 1)
    }
}