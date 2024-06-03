package es.upm.macroscore.ui.home.feed.adapter.adapter

import android.view.View
import android.view.View.VISIBLE
import android.view.View.GONE
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.databinding.ItemRecyclerViewMealBinding
import es.upm.macroscore.ui.home.feed.FoodState
import es.upm.macroscore.ui.model.FoodUIModel

class ItemMealViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemRecyclerViewMealBinding.bind(view)

    fun bind(foodUIModel: FoodUIModel) {
        val context = binding.root.context

        binding.foodName.isSelected = true
        binding.foodName.text = foodUIModel.name
        binding.foodKcal.text = context.getString(R.string.kcal_per_100, foodUIModel.kcalPer100)
        binding.foodCarbs.text = context.getString(R.string.carbs_per_100, foodUIModel.carbsPer100)
        binding.foodProts.text = context.getString(R.string.prots_per_100, foodUIModel.protsPer100)
        binding.foodFats.text = context.getString(R.string.fats_per_100, foodUIModel.fatsPer100)

        updateUIBasedOnState(foodUIModel.state)

        itemView.setOnClickListener {
            foodUIModel.state = if (foodUIModel.state == FoodState.COLLAPSED) FoodState.EXPANDED else FoodState.COLLAPSED
            updateUIBasedOnState(foodUIModel.state)
        }
    }

    private fun updateUIBasedOnState(state: FoodState) {
        when (state) {
            FoodState.COLLAPSED -> {
                binding.cardViewFoodMacros.visibility = GONE
            }
            FoodState.EXPANDED -> {
                binding.cardViewFoodMacros.visibility = VISIBLE
            }
        }
    }
}