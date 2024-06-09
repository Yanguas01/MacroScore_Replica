package es.upm.macroscore.ui.home.feed.food.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import es.upm.macroscore.R
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.ui.home.feed.adapter.FoodDiffUtil
import es.upm.macroscore.ui.model.FoodUIModel

class FoodAdapter(
    private val onItemSelected: (FoodUIModel) -> Unit
): PagingDataAdapter<FoodUIModel, FoodViewHolder>(FoodDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_meal, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = getItem(position)
        holder.bind(foodItem, onItemSelected)
    }
}