package es.upm.macroscore.ui.home.feed.adapter

import androidx.recyclerview.widget.DiffUtil
import es.upm.macroscore.ui.model.FoodUIModel
import es.upm.macroscore.ui.model.MealUIModel
class FoodDiffUtil : DiffUtil.ItemCallback<FoodUIModel>() {

    override fun areItemsTheSame(oldItem: FoodUIModel, newItem: FoodUIModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FoodUIModel, newItem: FoodUIModel): Boolean {
        return oldItem == newItem
    }
}

