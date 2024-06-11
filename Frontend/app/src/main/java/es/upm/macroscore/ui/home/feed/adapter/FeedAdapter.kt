package es.upm.macroscore.ui.home.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import es.upm.macroscore.R
import es.upm.macroscore.ui.home.feed.FeedViewModel
import es.upm.macroscore.ui.home.feed.meal.MealDiffUtil
import es.upm.macroscore.ui.model.MealUIModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FeedAdapter(
    private val viewModel: FeedViewModel,
    private val touchHelper: ItemTouchHelper,
    private val onUpdate: (position: Int) -> Unit,
    private val onEditMeal: (position: Int) -> Unit,
    private val onDeleteMeal: (mealId: String) -> Unit,
    private val onEditFood: (mealPosition: Int, foodPosition: Int) -> Unit,
    private val onDeleteFood: (mealPosition: Int, foodId: String) -> Unit,
    private val addFood: (String) -> Unit
) : ListAdapter<MealUIModel, FeedViewHolder>(MealDiffUtil()) {

    fun moveItem(fromPosition: Int, toPosition: Int) {
        viewModel.moveItem(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_view_feed, parent, false),
            onUpdate = onUpdate,
            onEditMeal = onEditMeal,
            onDeleteMeal = onDeleteMeal,
            onEditFood = onEditFood,
            onDeleteFood = onDeleteFood
        )
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position), touchHelper, addFood)
    }
}
