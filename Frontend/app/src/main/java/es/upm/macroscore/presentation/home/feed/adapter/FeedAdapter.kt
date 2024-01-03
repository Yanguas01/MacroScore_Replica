package es.upm.macroscore.presentation.home.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.presentation.home.feed.meal.MealDiffUtil
import java.util.Collections

class FeedAdapter(
    private var mealList: List<MealModel> = emptyList(),
    private val touchHelper: ItemTouchHelper,
    private val addFood: (String) -> Unit,
) : RecyclerView.Adapter<FeedViewHolder>() {

    fun updateList (newMealList: List<MealModel>) {
        val mealDiffUtil = MealDiffUtil(mealList, newMealList)
        val result = DiffUtil.calculateDiff(mealDiffUtil)
        mealList = newMealList
        result.dispatchUpdatesTo(this)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mealList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mealList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_feed, parent, false)
        )
    }

    override fun getItemCount() = mealList.size

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(mealList[position], touchHelper, addFood)
    }
}
