package es.upm.macroscore.ui.home.feed.meal

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import es.upm.macroscore.ui.model.MealUIModel

class MealDiffUtil : DiffUtil.ItemCallback<MealUIModel>() {
    override fun areItemsTheSame(oldItem: MealUIModel, newItem: MealUIModel): Boolean {
        Log.d("MealDiffUtil", "Are Items The Same?: ${oldItem.id == newItem.id}")
        Log.d("MealDiffUtil", "Old Item Id: ${oldItem.id}")
        Log.d("MealDiffUtil", "New Item Id: ${newItem.id}\n")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MealUIModel, newItem: MealUIModel): Boolean {
        Log.d("MealDiffUtil", "Are Contents The Same?: ${oldItem == newItem}")
        Log.d("MealDiffUtil", "Old Item: ${oldItem.toString()}")
        Log.d("MealDiffUtil", "New Item: ${newItem.toString()}\n")
        return oldItem == newItem
    }
}