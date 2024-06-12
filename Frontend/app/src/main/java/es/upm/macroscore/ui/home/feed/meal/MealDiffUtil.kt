package es.upm.macroscore.ui.home.feed.meal

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import es.upm.macroscore.ui.model.MealUIModel

class MealDiffUtil : DiffUtil.ItemCallback<MealUIModel>() {
    override fun areItemsTheSame(oldItem: MealUIModel, newItem: MealUIModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MealUIModel, newItem: MealUIModel): Boolean {
        return oldItem == newItem
    }
}