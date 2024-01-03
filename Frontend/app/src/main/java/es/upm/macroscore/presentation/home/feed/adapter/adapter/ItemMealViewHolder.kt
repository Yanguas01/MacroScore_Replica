package es.upm.macroscore.presentation.home.feed.adapter.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.databinding.ItemRecyclerViewMealBinding
import es.upm.macroscore.domain.model.FoodModel

class ItemMealViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemRecyclerViewMealBinding.bind(view)

    fun bind(foodModel: FoodModel) {
        val context = binding.root.context

        binding.foodName.text = foodModel.name
        binding.foodKcal.text = context.getString(R.string.kcal_per_100, foodModel.kcalPer100)
        binding.foodCarbs.text = context.getString(R.string.carbs_per_100, foodModel.carbsPer100)
        binding.foodProts.text = context.getString(R.string.prots_per_100, foodModel.protsPer100)
        binding.foodFats.text = context.getString(R.string.fats_per_100, foodModel.fatsPer100)
    }
}