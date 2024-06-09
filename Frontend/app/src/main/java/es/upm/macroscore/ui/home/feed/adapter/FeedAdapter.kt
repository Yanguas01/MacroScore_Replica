package es.upm.macroscore.ui.home.feed.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import es.upm.macroscore.R
import es.upm.macroscore.ui.home.feed.FeedViewModel
import es.upm.macroscore.ui.home.feed.meal.MealDiffUtil
import es.upm.macroscore.ui.model.MealUIModel
import kotlinx.coroutines.flow.onEach
import java.util.Collections

class FeedAdapter(
    private val viewModel: FeedViewModel,
    private var mealList: List<MealUIModel> = emptyList(),
    private val touchHelper: ItemTouchHelper,
    private val onEditMeal: (position: Int) -> Unit,
    private val onDeleteMeal: (position: Int) -> Unit,
    private val addFood: (String) -> Unit
) : RecyclerView.Adapter<FeedViewHolder>() {

    init {
        viewModel.mealList.onEach { list ->
            val mealDiffUtil = MealDiffUtil(mealList, list)
            val result = DiffUtil.calculateDiff(mealDiffUtil)
            mealList = list
            result.dispatchUpdatesTo(this)
        }
    }

    fun updateList (newMealList: List<MealUIModel>) {
        Log.d("FeedAdapter", mealList.toString())
        Log.d("FeedAdapter", newMealList.toString())

        Log.d("FeedAdapter", (mealList == newMealList).toString())

        val mealDiffUtil = MealDiffUtil(mealList, newMealList)
        val result = DiffUtil.calculateDiff(mealDiffUtil)

        mealList = newMealList
        result.dispatchUpdatesTo(this)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        viewModel.moveItem(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_feed, parent, false),
            onUpdate = { position ->
                notifyItemChanged(position)
            },
            onEditMeal = onEditMeal,
            onDeleteMeal = onDeleteMeal
        )
    }

    override fun getItemCount() = mealList.size

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(mealList[position], touchHelper, addFood)
    }
}
