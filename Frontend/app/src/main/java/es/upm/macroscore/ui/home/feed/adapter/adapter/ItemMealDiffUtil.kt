package es.upm.macroscore.ui.home.feed.adapter.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import es.upm.macroscore.ui.model.FoodUIModel

class ItemMealDiffUtil: DiffUtil.ItemCallback<FoodUIModel>() {
    override fun areItemsTheSame(oldItem: FoodUIModel, newItem: FoodUIModel): Boolean {
        Log.i("ItemMealDiffUtil", "Are Items The Same?: ${oldItem.id == newItem.id}")
        Log.i("ItemMealDiffUtil", "Old Item Id: ${oldItem.id}")
        Log.i("ItemMealDiffUtil", "New Item Id: ${newItem.id}\n")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FoodUIModel, newItem: FoodUIModel): Boolean {
        Log.i("ItemMealDiffUtil", "Are Contents The Same?: ${oldItem.id == newItem.id}")
        Log.i("ItemMealDiffUtil", "Old Item: ${oldItem.toString()}")
        Log.i("ItemMealDiffUtil", "New Item: ${newItem.toString()}\n\n")
        return oldItem == newItem
    }
}