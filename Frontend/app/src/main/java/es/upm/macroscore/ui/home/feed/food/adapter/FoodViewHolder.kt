package es.upm.macroscore.ui.home.feed.food.adapter

import android.view.View
import android.view.View.GONE
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.databinding.ItemRecyclerViewMealBinding
import es.upm.macroscore.domain.model.FoodModel

class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemRecyclerViewMealBinding.bind(view)

    fun bind(foodModel: FoodModel, onItemSelected: (FoodModel) -> Unit) {
        binding.buttonMore.visibility = GONE
        binding.cardViewFoodMacros.visibility = GONE
        binding.root.setOnClickListener { onItemSelected(foodModel) }

        val context = binding.root.context

        binding.foodName.isSelected = true
        binding.foodName.text = foodModel.name
    }
}
