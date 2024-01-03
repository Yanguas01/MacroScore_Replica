package es.upm.macroscore.presentation.home.feed.meal

import androidx.recyclerview.widget.DiffUtil
import es.upm.macroscore.domain.model.MealModel

class MealDiffUtil(
    private val oldList: List<MealModel>,
    private val newList: List<MealModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].name == newList[newItemPosition].name

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}