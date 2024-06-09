package es.upm.macroscore.ui.home.feed.food.adapter

import android.view.View
import android.view.View.GONE
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.databinding.ItemRecyclerViewMealBinding
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.ui.model.FoodUIModel

class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemRecyclerViewMealBinding.bind(view)

    fun bind(foodModel: FoodUIModel?, onItemSelected: (FoodUIModel) -> Unit) {
        binding.buttonMore.visibility = GONE
        binding.cardViewFoodMacros.visibility = GONE
        binding.root.setOnClickListener {
            if (foodModel != null) {
                onItemSelected(foodModel)
            }
        }

        binding.foodName.isSelected = true
        if (foodModel != null) {
            binding.foodName.text = foodModel.name
        }
    }
}
