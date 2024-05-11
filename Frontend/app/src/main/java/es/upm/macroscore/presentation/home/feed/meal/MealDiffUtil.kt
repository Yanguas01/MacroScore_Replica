package es.upm.macroscore.presentation.home.feed.meal

import androidx.recyclerview.widget.DiffUtil
import es.upm.macroscore.presentation.model.MealUIModel

class MealDiffUtil(
    private val oldList: List<MealUIModel>,
    private val newList: List<MealUIModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].name == newList[newItemPosition].name

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}